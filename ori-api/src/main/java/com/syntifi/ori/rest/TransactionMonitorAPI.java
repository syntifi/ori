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
import com.syntifi.ori.dto.AMLRulesDTO;
import com.syntifi.ori.dto.TransactionDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TransactionMapper;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.TransactionRepository;
import com.syntifi.ori.service.AMLService;
import com.syntifi.ori.service.TransactionService;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Ori Rest Api for transaction monitor endpoints
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Singleton
@Unremovable
@RegisterForReflection
@Tag(name = "Transaction monitor", description = "Monitor accounts, trace transactions and calculate risk scores")
public class TransactionMonitorAPI extends AbstractBaseRestApi {

        @Inject
        TransactionRepository transactionRepository;

        @Inject
        TransactionService transactionService;

        @Inject
        AMLService amlService;

        /**
         * GET method to return the different AML scores as a double between 0 and 1
         * 
         * @param symbol
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
        @Path("{tokenSymbol}/score/{account}")
        public AMLRulesDTO scoreAccount(@PathParam("tokenSymbol") String symbol,
                        @PathParam("account") String account,
                        @QueryParam("date") @LocalDateTimeFormat LocalDateTime dateTime,
                        @QueryParam("longWindow") Integer longWindow,
                        @QueryParam("midWindow") Integer midWindow,
                        @QueryParam("shortWindow") Integer shortWindow,
                        @QueryParam("threshold") Double threshold) throws ORIException {
                getTokenOr404(symbol);
                getAccountOr404(symbol, account);
                int maxWindow = ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class);
                int window = longWindow == null
                                ? maxWindow
                                : Math.min(longWindow, maxWindow);
                OffsetDateTime date = dateTime == null ? OffsetDateTime.now() : dateTime.atOffset(ZoneOffset.of("Z"));
                OffsetDateTime from = date.minusDays(window);
                List<Transaction> in = transactionRepository.getIncomingTransactions(symbol, account, from, date);
                List<Transaction> out = transactionRepository.getOutgoingTransactions(symbol, account, from, date);
                return amlService.calculateScores(in, out, threshold, shortWindow, longWindow);
        }

        /**
         * GET method to retrieve the graph of all past-transactions linked to the given
         * account
         * 
         * @param symbol
         * @param account
         * @param fromDate
         * @param toDate
         * @param window
         * @return
         * @throws ORIException
         */
        @GET
        @Path("{tokenSymbol}/traceCoin/back/{account}")
        public List<TransactionDTO> reverseGraphWalk(@PathParam("tokenSymbol") String symbol,
                        @PathParam("account") String account,
                        @QueryParam("fromDate") @LocalDateTimeFormat LocalDateTime fromDate,
                        @QueryParam("toDate") @LocalDateTimeFormat LocalDateTime toDate,
                        @QueryParam("window") Integer window) throws ORIException {
                getTokenOr404(symbol);
                getAccountOr404(symbol, account);
                int maxWindow = ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class);
                int wind = window == null ? maxWindow : Math.min(window, maxWindow);
                OffsetDateTime to = toDate == null ? OffsetDateTime.now() : toDate.atOffset(ZoneOffset.of("Z"));
                OffsetDateTime from = fromDate == null
                                ? to.minusDays(wind)
                                : fromDate.atOffset(ZoneOffset.of("Z"));
                return transactionService.reverseGraphWalk(symbol, account, from, to)
                                .stream()
                                .map(TransactionMapper::fromModel)
                                .collect(Collectors.toList());
        }

        /**
         * GET method to retrieve the graph of all forward-transactions linked to the
         * given account
         * 
         * @param symbol
         * @param account
         * @param fromDate
         * @param toDate
         * @param window
         * @return
         * @throws ORIException
         */
        @GET
        @Path("{tokenSymbol}/traceCoin/forward/{account}")
        public List<TransactionDTO> forwardGraphWalk(@PathParam("tokenSymbol") String symbol,
                        @PathParam("account") String account,
                        @QueryParam("fromDate") @LocalDateTimeFormat LocalDateTime fromDate,
                        @QueryParam("toDate") @LocalDateTimeFormat LocalDateTime toDate,
                        @QueryParam("window") Integer window) throws ORIException {
                getTokenOr404(symbol);
                getAccountOr404(symbol, account);
                int maxWindow = ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class);
                int wind = window == null ? maxWindow : Math.min(window, maxWindow);
                OffsetDateTime from = fromDate == null ? OffsetDateTime.now() : fromDate.atOffset(ZoneOffset.of("Z"));
                OffsetDateTime to = toDate == null
                                ? from.plusDays(wind)
                                : toDate.atOffset(ZoneOffset.of("Z"));
                return transactionService.forwardGraphWalk(symbol, account, from, to)
                                .stream()
                                .map(TransactionMapper::fromModel)
                                .collect(Collectors.toList());
        }
}
