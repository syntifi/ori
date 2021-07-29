package com.syntifi.ori.rest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

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
    public HashMap<String, Double> scoreAccount(@PathParam("account") String account) {
        AMLRules rules = new AMLRules();
        HashMap<String, Double> scores = new HashMap<>();
        scores.put("StructuringOverTime", rules.structuringOverTimeScore());
        scores.put("HighVolume", rules.highVolumeScore());
        scores.put("UnusualVolume", rules.unusualVolumeScore());
        scores.put("UnusualBehaviour", rules.unusualBehaviourScore());
        scores.put("FlowThrough", rules.flowThroughScore());
        return scores ;
    }

    @GET
    @Path("graphWalk/back/{account}")
    public List<Transaction> reverseGraphWalk(@PathParam("account") String account, 
            @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) throws IOException, WebApplicationException {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    formatter.withZone(ZoneId.of("GMT"));
                    LocalDateTime from = fromDate != null 
                                ? LocalDateTime.parse(fromDate, formatter) 
                                : LocalDateTime.now(); 
                    LocalDateTime to = toDate != null 
                                ? LocalDateTime.parse(toDate, formatter) 
                                : from.minusMonths(1);
                    return transactionService.reverseGraphWalk(account, from, to);
            }

    @GET
    @Path("graphWalk/forward/{account}")
    public List<Transaction> forwardGraphWalk(@PathParam("account") String account, 
            @QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) throws IOException, WebApplicationException {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    formatter.withZone(ZoneId.of("GMT"));
                    LocalDateTime from = fromDate != null 
                                ? LocalDateTime.parse(fromDate, formatter) 
                                : LocalDateTime.now(); 
                    LocalDateTime to = toDate != null 
                                ? LocalDateTime.parse(toDate, formatter) 
                                : from.minusMonths(1);
                    return transactionService.forwardGraphWalk(account, from, to);
            }
}
