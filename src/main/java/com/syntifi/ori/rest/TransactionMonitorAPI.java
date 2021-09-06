package com.syntifi.ori.rest;

import java.time.LocalDateTime;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.vertx.core.cli.annotations.Description;

import com.syntifi.ori.service.TransactionService;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.AMLRules;
import com.syntifi.ori.converter.LocalDateTimeFormat;

import javax.inject.Inject;


@Path("/")
@Tag(name = "Transaction monitor", description = "Monitor accounts, trace transactions and calculate risk scores")
public class TransactionMonitorAPI {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    @Inject
    TransactionService transactionService;

    @GET
    @Path("score/{account}")
    public AMLRules scoreAccount(@PathParam("account") String account, 
                        @QueryParam("date") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime date) throws ORIException {
        try {
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

    @GET
    @Path("traceCoin/back/{account}")
    @Description("Traces")
    public List<Transaction> reverseGraphWalk(@PathParam("account") String account, 
            @QueryParam("fromDate") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime from, 
            @QueryParam("toDate") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime to) throws ORIException {
        try {
            return transactionService.reverseGraphWalk(account, from, to);
        } catch (ORIException e) {
            throw e;
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

    @GET
    @Path("traceCoin/forward/{account}")
    public List<Transaction> forwardGraphWalk(@PathParam("account") String account, 
            @QueryParam("fromDate") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime from, 
            @QueryParam("toDate") @LocalDateTimeFormat(DATE_FORMAT) LocalDateTime to) throws ORIException {
        try {
            return transactionService.forwardGraphWalk(account, from, to);
        } catch (ORIException e) {
            throw e;
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }
}
