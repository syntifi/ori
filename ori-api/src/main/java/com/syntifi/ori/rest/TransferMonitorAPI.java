package com.syntifi.ori.rest;

import com.syntifi.ori.converter.LocalDateTimeFormat;
import com.syntifi.ori.dto.AMLRulesDTO;
import com.syntifi.ori.dto.TransferDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TransferMapper;
import com.syntifi.ori.model.Transfer;
import com.syntifi.ori.repository.TransferRepository;
import com.syntifi.ori.service.AMLService;
import com.syntifi.ori.service.TransferService;
import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ori Rest Api for transfer monitor endpoints
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Unremovable
@RegisterForReflection
@Tag(name = "Transfer monitor", description = "Monitor accounts, trace transfers and calculate risk scores")
public class TransferMonitorAPI extends AbstractBaseRestApi {

    @Inject
    TransferRepository transferRepository;

    @Inject
    TransferService transferService;

    @Inject
    AMLService amlService;

    /**
     * GET method to return the different AML scores as a double between 0 and 1
     *
     * @param chain:w
     *
     * @param account
     * @param dateTime
     * @param longWindow
     * @param midWindow
     * @param shortWindow
     * @param threshold
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/score/{account}")
    public AMLRulesDTO scoreAccount(
            @PathParam("chain") String chain,
            @PathParam("account") String account,
            @QueryParam("date") @LocalDateTimeFormat LocalDateTime dateTime,
            @QueryParam("longWindow") Integer longWindow,
            @QueryParam("midWindow") Integer midWindow,
            @QueryParam("shortWindow") Integer shortWindow,
            @QueryParam("threshold") Double threshold) throws ORIException {
        getAccountOr404(chain, account);
        int maxWindow = ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class);
        int window = longWindow == null
                ? maxWindow
                : Math.min(longWindow, maxWindow);
        OffsetDateTime date = dateTime == null ? OffsetDateTime.now() : dateTime.atOffset(ZoneOffset.of("Z"));
        OffsetDateTime from = date.minusDays(window);
        List<Transfer> in = transferRepository.getIncomingTransfers(chain, account, from, date).list();
        List<Transfer> out = transferRepository.getOutgoingTransfers(chain, account, from, date).list();
        return amlService.calculateScores(in, out, threshold, shortWindow, longWindow);
    }

    /**
     * GET method to retrieve the graph of all past-transfers linked to the given
     * account
     *
     * @param chain
     * @param account
     * @param fromDate
     * @param toDate
     * @param window
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/trace/back/{account}")
    public List<TransferDTO> reverseGraphWalk(@PathParam("chain") String chain,
                                              @PathParam("account") String account,
                                              @QueryParam("fromDate") @LocalDateTimeFormat LocalDateTime fromDate,
                                              @QueryParam("toDate") @LocalDateTimeFormat LocalDateTime toDate,
                                              @QueryParam("window") Integer window) throws ORIException {
        getAccountOr404(chain, account);
        int maxWindow = ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class);
        int wind = window == null ? maxWindow : Math.min(window, maxWindow);
        OffsetDateTime to = toDate == null ? OffsetDateTime.now() : toDate.atOffset(ZoneOffset.of("Z"));
        OffsetDateTime from = fromDate == null
                ? to.minusDays(wind)
                : fromDate.atOffset(ZoneOffset.of("Z"));
        return transferService.reverseGraphWalk(chain, account, from, to)
                .stream()
                .map(TransferMapper::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * GET method to retrieve the graph of all forward-transfers linked to the
     * given account
     *
     * @param chain
     * @param account
     * @param fromDate
     * @param toDate
     * @param window
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/trace/forward/{account}")
    public List<TransferDTO> forwardGraphWalk(@PathParam("chain") String chain,
                                              @PathParam("account") String account,
                                              @QueryParam("fromDate") @LocalDateTimeFormat LocalDateTime fromDate,
                                              @QueryParam("toDate") @LocalDateTimeFormat LocalDateTime toDate,
                                              @QueryParam("window") Integer window) throws ORIException {
        getAccountOr404(chain, account);
        int maxWindow = ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class);
        int wind = window == null ? maxWindow : Math.min(window, maxWindow);
        OffsetDateTime from = fromDate == null ? OffsetDateTime.now() : fromDate.atOffset(ZoneOffset.of("Z"));
        OffsetDateTime to = toDate == null
                ? from.plusDays(wind)
                : toDate.atOffset(ZoneOffset.of("Z"));
        return transferService.forwardGraphWalk(chain, account, from, to)
                .stream()
                .map(TransferMapper::fromModel)
                .collect(Collectors.toList());
    }
}
