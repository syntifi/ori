package com.syntifi.ori.rest;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.syntifi.ori.converter.LocalDateTimeFormat;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.AMLRules;
import com.syntifi.ori.service.TransactionService;

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
public class TransactionMonitorAPI {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Inject
    TransactionService transactionService;

    /**
     * GET method to return the different AML scores in [0,1] 
     * 
     * @param account
     * @param date
     * @return {@link AMLRules}
     * @throws ORIException
     */
    @GET
    @Path("score/{account}")
    public AMLRules scoreAccount(@PathParam("account") String account, 
                        @QueryParam("date") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime dateTime) throws ORIException {
        try {
            LocalDateTime date = dateTime==null ? LocalDateTime.now() : dateTime;
            LocalDateTime from = date.minusDays(ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class));
            List<Transaction> in = transactionService.getIncomingTransactions(account, from, date);
            List<Transaction> out = transactionService.getOutgoingTransactions(account, from, date);
            AMLRules rules = new AMLRules(in, out);
            rules.calculateScores();
            return rules;
        } catch (ORIException e) {
            throw e;
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     * GET method to retrieve the graph of all past-transactions linked to the given account 
     * 
     * @param account
     * @param from 
     * @param to
     * @return List<Transaction>
     * @throws ORIException
     */
    @GET
    @Path("traceCoin/back/{account}")
    @Description("Traces")
    public List<Transaction> reverseGraphWalk(@PathParam("account") String account, 
            @QueryParam("fromDate") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime fromDate, 
            @QueryParam("toDate") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime toDate) throws ORIException {
        try {
            LocalDateTime to = toDate == null ? LocalDateTime.now() : toDate;
            LocalDateTime from = fromDate==null 
                    ? to.minusDays(ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class))
                    : fromDate;
            return transactionService.reverseGraphWalk(account, from, to);
        } catch (ORIException e) {
            throw e;
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     * GET method to retrieve the graph of all forward-transactions linked to the given account 
     * 
     * @param account
     * @param from 
     * @param to
     * @return List<Transaction>
     * @throws ORIException
     */
    @GET
    @Path("traceCoin/forward/{account}")
    public List<Transaction> forwardGraphWalk(@PathParam("account") String account, 
            @QueryParam("fromDate") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime fromDate, 
            @QueryParam("toDate") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime toDate) throws ORIException {
        try {
            LocalDateTime to = toDate == null ? LocalDateTime.now() : toDate;
            LocalDateTime from = fromDate==null 
                    ? to.minusDays(ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class))
                    : fromDate;
            return transactionService.forwardGraphWalk(account, from, to);
        } catch (ORIException e) {
            throw e;
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }
}
