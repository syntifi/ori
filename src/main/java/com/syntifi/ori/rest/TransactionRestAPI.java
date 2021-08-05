
package com.syntifi.ori.rest;

import java.util.ArrayList;
import java.util.List;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import io.vertx.core.json.JsonObject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.TransactionService;

import javax.inject.Inject;

@Path("/transaction")
@Tag(name = "Transaction", description = "Transaction resources")
public class TransactionRestAPI {
    
    private static final Logger LOG = Logger.getLogger(TransactionRestAPI.class);

    @Inject
    TransactionService transactionService;

    @POST
    public Response index(Transaction transaction) throws ORIException {
        if (transaction.hash == null) {
            throw new ORIException("Transaction hash missing", 404);
        } try {
            transactionService.index(transaction);
        } catch (Exception e) {
            LOG.error(e);
            throw transactionService.parseElasticError(e);
        }
        return Response.ok(new JsonObject().put("created", URI.create("/transaction/" + transaction.hash))).build();
    }

    @GET
    public List<Transaction> getAllTransactions(@QueryParam("fromAccount") String from,
            @QueryParam("toAccount") String to) throws ORIException {
        try {
            List<Transaction> out = new ArrayList<>();
            if ((from != null) && (to == null)) {
                out = transactionService.getOutgoingTransactions(from);
            } else if ((from == null) && (to != null)) {
                out = transactionService.getIncomingTransactions(to);
            } else if ((from != null) && (to != null)) {
                out = transactionService.getTransactionsFromAccountToAccount(from, to);
            } else {
                out = transactionService.getAllTransactions();
            }
            return out.subList(0, Math.min(100, out.size()));
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

    @GET
    @Path("/{hash}")
    public Transaction getTransactionByHash(@PathParam("hash") String hash) throws ORIException {
        try {
            return transactionService.getTransactionByHash(hash);
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }

    @GET
    @Path("/account/{account}")
    public List<Transaction> getTransactionsByAccount(@PathParam("account") String account) throws ORIException {
        try {
            return transactionService.getAllTransactionsByAccount(account).subList(0,100);
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }

    @DELETE
    public Response clear() throws ORIException {
        try {
            return Response.ok(transactionService.clear().getRequestLine()).build();
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }

    @DELETE
    @Path("/{hash}")
    public Response delete(@PathParam("hash") String hash) throws ORIException {
        try {
            return Response.ok(transactionService.delete(hash).getRequestLine()).build();
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }


}