package com.syntifi.ori.rest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.syntifi.ori.converter.LocalDateTimeFormat;
import com.syntifi.ori.dto.TransactionDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TransactionMapper;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.TransactionRepository;
import com.syntifi.ori.service.AMLRules;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.vertx.core.cli.annotations.Description;

/**
 * REST API transaction monitor endpoints
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Transaction monitor", description = "Monitor accounts, trace transactions and calculate risk scores")
public class TransactionMonitorAPI extends AbstractBaseRestApi {

    @Inject
    TransactionRepository transactionRepository;

    /**
     * GET method to return the different AML scores in [0,1]
     * 
     * @param account
     * @param dateTime
     * @return {@link AMLRules}
     * @throws ORIException
     */
    @GET
    @Path("{tokenSymbol}/score/{account}")
    public AMLRules scoreAccount(@PathParam("tokenSymbol") String symbol, @PathParam("account") String account,
            @QueryParam("date") @LocalDateTimeFormat LocalDateTime dateTime) throws ORIException {
        getTokenOr404(symbol);
        getAccountOr404(symbol, account);
        OffsetDateTime date = dateTime == null ? OffsetDateTime.now() : dateTime.atOffset(ZoneOffset.of("Z"));
        OffsetDateTime from = date.minusDays(ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class));
        List<Transaction> in = transactionRepository.getIncomingTransactions(symbol, account, from, date);
        List<Transaction> out = transactionRepository.getOutgoingTransactions(symbol, account, from, date);
        AMLRules rules = new AMLRules(in, out);
        rules.calculateScores();
        return rules;
    }

    /**
     * GET method to retrieve the graph of all past-transactions linked to the given
     * account
     * 
     * @param account
     * @param fromDate
     * @param toDate
     * @return a list of {@link Transaction}
     * @throws ORIException
     */
    @GET
    @Path("{tokenSymbol}/traceCoin/back/{account}")
    @Description("Traces")
    public List<TransactionDTO> reverseGraphWalk(@PathParam("tokenSymbol") String symbol,
            @PathParam("account") String account,
            @QueryParam("fromDate") @LocalDateTimeFormat LocalDateTime fromDate,
            @QueryParam("toDate") @LocalDateTimeFormat LocalDateTime toDate) throws ORIException {
        getTokenOr404(symbol);
        getAccountOr404(symbol, account);
        OffsetDateTime to = toDate == null ? OffsetDateTime.now() : toDate.atOffset(ZoneOffset.of("Z"));
        OffsetDateTime from = fromDate == null
                ? to.minusDays(ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class))
                : fromDate.atOffset(ZoneOffset.of("Z"));
        return transactionRepository.reverseGraphWalk(account, from, to)
                .stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * GET method to retrieve the graph of all forward-transactions linked to the
     * given account
     * 
     * @param account
     * @param fromDate
     * @param toDate
     * @return a list of {@link Transaction}
     * @throws ORIException
     */
    @GET
    @Path("{tokenSymbol}/traceCoin/forward/{account}")
    public List<TransactionDTO> forwardGraphWalk(@PathParam("tokenSymbol") String symbol,
            @PathParam("account") String account,
            @QueryParam("fromDate") @LocalDateTimeFormat LocalDateTime fromDate,
            @QueryParam("toDate") @LocalDateTimeFormat LocalDateTime toDate) throws ORIException {
        getTokenOr404(symbol);
        getAccountOr404(symbol, account);
        OffsetDateTime to = toDate == null ? OffsetDateTime.now() : toDate.atOffset(ZoneOffset.of("Z"));
        OffsetDateTime from = fromDate == null
                ? to.minusDays(ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class))
                : fromDate.atOffset(ZoneOffset.of("Z"));
        return transactionRepository.forwardGraphWalk(account, from, to)
                .stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());
    }
}
