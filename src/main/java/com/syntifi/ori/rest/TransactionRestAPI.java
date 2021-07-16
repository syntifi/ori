
package com.syntifi.ori.rest;

import java.io.IOException;
import java.util.List;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.TransactionService;

import javax.inject.Inject;

@Path("/transaction")
@Tag(name = "Transaction", description = "Transaction resources")
public class TransactionRestAPI {
    @Inject
    TransactionService transactionService;

    @POST
    public Response index(Transaction transaction) throws IOException {
        if (transaction.hash == null) {
            throw new BadRequestException("Transaction hash missing");
        }
        transactionService.index(transaction);
        return Response.created(URI.create("/transaction/" + transaction.hash)).build();
    }

    @GET
    public List<Transaction> getAllTransactions(@QueryParam("fromAccount") String from,
            @QueryParam("toAccount") String to) throws IOException {
        if ((from != null) && (to == null)) {
            return transactionService.getOutgoingTransactions(from);
        } else if ((from == null) && (to != null)) {
            return transactionService.getIncomingTransactions(to);
        } else if ((from != null) && (to != null)) {
            return transactionService.getTransactionsFromAccountToAccount(from, to);
        } else {
            return transactionService.getAllTransactions();
        }
    }

    @DELETE
    public Response clear() throws IOException {
        return Response.ok(transactionService.clear().toString()).build();
    }

    @DELETE
    @Path("/{hash}")
    public Response delete(String hash) throws IOException {
        return Response.ok(transactionService.delete(hash).toString()).build();
    }

    @GET
    @Path("/{hash}")
    public Transaction getTransactionByHash(@PathParam("hash") String hash) throws IOException {
        return transactionService.getTransactionByHash(hash);
    }

    @GET
    @Path("/account/{account}")
    public List<Transaction> getTransactionsByAccount(@PathParam("account") String account) throws IOException {
        return transactionService.getAllTransactionsByAccount(account);
    }
}