
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

import io.vertx.core.json.JsonObject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.TransactionService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * REST API transaction endpoints 
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Transaction", description = "Transaction resources")
public class TransactionRestAPI {
    
    @Inject
    TransactionService transactionService;

    /**
     * POST method to add and index a new transactions in ES
     *  
     * @param transaction 
     * @return Response
     * @throws ORIException
     */
    @POST
    public Response index(Transaction transaction) throws ORIException {
        if (transaction.hash == null) {
            throw new ORIException("Transaction hash missing", 404);
        } try {
            transactionService.index(transaction);
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
        return Response.ok(new JsonObject().put("created", URI.create("/transaction/" + transaction.hash))).build();
    }

     /**
     * GET method to retreive all transactions indexed in ES originating from one account 
     * to another account. Note that the transactions are sorted in reverse chronological order
     * 
     * @param from
     * @param to
     * @return List<Transaction>
     * @throws ORIException
     */
    @GET
    public List<Transaction> getAllTransactions(@QueryParam("fromAccount") String from,
            @QueryParam("toAccount") String to) throws ORIException {
        try {
            List<Transaction> transactions = new ArrayList<>();
            if ((from != null) && (to == null)) {
                transactions = transactionService.getOutgoingTransactions(from);
            } else if ((from == null) && (to != null)) {
                transactions = transactionService.getIncomingTransactions(to);
            } else if ((from != null) && (to != null)) {
                transactions = transactionService.getTransactionsFromAccountToAccount(from, to);
            } else {
                transactions = transactionService.getAllTransactions();
            }
            return transactions.subList(0, Math.min(100, transactions.size()));
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     * GET method to retrieve a transaction by it's hash. Hash is given as a hex string.
     * 
     * @param hash
     * @return Transaction 
     * @throws ORIException
     */
    @GET
    @Path("/{hash}")
    public Transaction getTransactionByHash(@PathParam("hash") String hash) throws ORIException {
        try {
            return transactionService.getTransactionByHash(hash);
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     * GET method to retrieve all incoming and outgoing transactions in a given account
     *  
     * @param account
     * @return List<Transaction>
     * @throws ORIException
     */
    @GET
    @Path("/account/{account}")
    public List<Transaction> getTransactionsByAccount(@PathParam("account") String account) throws ORIException {
        try {
            var transactions = transactionService.getAllTransactionsByAccount(account);
            return transactions.subList(0, Math.min(100, transactions.size()));
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     * DELETE method to remove all transactions and clean the database
     * 
     * @return Response
     * @throws ORIException
     */
    @DELETE
    public Response clear() throws ORIException {
        try {
            transactionService.clear();
            return Response.ok(new JsonObject()
                                        .put("method", "DELETE")
                                        .put("uri", "/transaction"))
                            .build();
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     *  DELETE method to remove a specific transaction given the hash. Hash is given as a hex string
     * 
     * @param hash
     * @return
     * @throws ORIException
     */
    @DELETE
    @Path("/{hash}")
    public Response delete(@PathParam("hash") String hash) throws ORIException {
        try {
            transactionService.delete(hash);
            return Response.ok(new JsonObject()
                                        .put("method", "DELETE")
                                        .put("uri", "/transaction/" + hash))
                            .build();
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }


}