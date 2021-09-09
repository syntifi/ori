package com.syntifi.ori.graphql;

import java.util.List;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Description;
import io.smallrye.graphql.api.Subscription;

import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import javax.ws.rs.QueryParam;

import io.smallrye.mutiny.Multi;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.TransactionService;

import javax.inject.Inject;
import javax.json.stream.JsonParsingException;

/**
 * Graph API queries to retrieve transactions
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@GraphQLApi
public class TransactionGraphQLAPI {

    private static final BroadcastProcessor<Transaction> processor = BroadcastProcessor.create(); 

    @Inject
    TransactionService transactionService;

    /**
     * Query a transaction given a hash 
     *  
     * @param hash
     * @return Transaction
     * @throws ORIException
     */
    @Query
    @Description("Get a transaction given its hash")
    public Transaction getTransaction(String hash) throws ORIException{
        try {
            if (hash == null) {
                throw new ORIException("Transaction hash missing", 404);
            }
            return transactionService.getTransactionByHash(hash);
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     * Query to retrieve the transaction (optional) from, (optional) to an account 
     * in reverse chronological order. Returns all transactions if neither from nor to 
     * is specified.
     * 
     * @param from
     * @param to
     * @return List<Transaction>
     * @throws ORIException
     */
    @Query
    @Description("Return the transaction (optional) from, (optional) to an account. Return all transactions if neither from nor to is specified.")
    public List<Transaction> getTransactions(@QueryParam("from") String from, @QueryParam("to") String to)
            throws ORIException {
        try {
            if ((from != null) && (to == null)) {
                return transactionService.getOutgoingTransactions(from);
            } else if ((from == null) && (to != null)) {
                return transactionService.getIncomingTransactions(to);
            } else if ((from != null) && (to != null)) {
                return transactionService.getTransactionsFromAccountToAccount(from, to);
            } else {
                return transactionService.getAllTransactions();
            }
        } catch (Exception e){
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     * Query to retrieve all transactions (incoming or outgoing) in reverse chronological order.
     *  
     * @param account
     * @return List<Transaction>
     * @throws ORIException
     */
    @Query
    @Description("Return all transactions for a given account")
    public List<Transaction> getAccount(String account) throws ORIException {
        try {
            if (account == null) {
                throw new ORIException("Account missing", 404);
            }
            return transactionService.getAllTransactionsByAccount(account);
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     *  Mutation type query to add and index a new transaction in ES
     * 
     * @param transaction
     * @return Transaction
     * @throws ORIException
     */
    @Mutation
    @Description("Add a new transaction")
    public Transaction addTransaction(Transaction transaction) throws ORIException {
        try {
            transactionService.index(transaction);
            return transactionService.getTransactionByHash(transaction.hash);
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

    /**
     * Subscription type query that issues an event everytime a new transaction is created
     * (STILL EXPERIMENTAL)
     * 
     * @return
     */
    @Subscription
    public Multi<Transaction> transactionCreated(){
        return processor; 
    }

    /**
     * Mutation type query to remove a transaction from ES given a hash
     *  
     * @param hash
     * @return
     * @throws ORIException
     */
    @Mutation
    @Description("Remove a transaction")
    public Transaction deleteTransaction(String hash) throws ORIException {
        try {
            transactionService.delete(hash);
            return transactionService.getTransactionByHash(hash);
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }
}
