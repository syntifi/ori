package com.syntifi.ori.rest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.syntifi.ori.service.TransactionService;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.AMLRules;

import javax.inject.Inject;

import org.jboss.logging.Logger;


@Path("/")
@Tag(name = "Transaction monitor", description = "Monitor accounts, trace transactions and calculate risk scores")
public class TransactionMonitorAPI {

    private static final Logger LOG = Logger.getLogger(TransactionMonitorAPI.class);
    
    @Inject
    TransactionService transactionService;

    @GET
    @Path("score/{account}")
    public AMLRules scoreAccount(@PathParam("account") String account, 
                                @QueryParam("date") String date) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    formatter.withZone(ZoneId.of("GMT"));
        LocalDateTime to = date != null 
                                ? LocalDateTime.parse(date, formatter) 
                                : LocalDateTime.now(); 
        LocalDateTime from = to.minusDays(ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class));
        LOG.info("=========");
        LOG.info(from);
        LOG.info(to);
        List<Transaction> in = transactionService.getIncomingTransactions(account, from, to);
        List<Transaction> out = transactionService.getOutgoingTransactions(account, from, to);
        LOG.info(in.size());
        LOG.info(out.size());
        AMLRules rules = new AMLRules(in, out);
        rules.calculateScores();
        return rules;
    }

    @GET
    @Path("graphWalk/back/{account}")
    public List<Transaction> reverseGraphWalk(@PathParam("account") String account, 
            @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) throws IOException, WebApplicationException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        formatter.withZone(ZoneId.of("GMT"));
        LocalDateTime to = toDate != null 
                ? LocalDateTime.parse(toDate, formatter) 
                : LocalDateTime.now(); 
        LocalDateTime from = fromDate != null 
                ? LocalDateTime.parse(fromDate, formatter) 
                : to.minusMonths(1);
        return transactionService.reverseGraphWalk(account, from, to);
    }

    @GET
    @Path("graphWalk/forward/{account}")
    public List<Transaction> forwardGraphWalk(@PathParam("account") String account, 
            @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) throws IOException, WebApplicationException {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    formatter.withZone(ZoneId.of("GMT"));
                    LocalDateTime to = toDate != null 
                                ? LocalDateTime.parse(toDate, formatter) 
                                : LocalDateTime.now(); 
                    LocalDateTime from = fromDate != null 
                                ? LocalDateTime.parse(fromDate, formatter) 
                                : to.minusMonths(1);
                    return transactionService.forwardGraphWalk(account, from, to);
            }
}
