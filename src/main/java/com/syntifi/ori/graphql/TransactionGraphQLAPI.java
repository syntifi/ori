package com.syntifi.ori.graphql;

import java.io.IOException;
import java.util.List;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Description;
import io.smallrye.graphql.api.Subscription;

import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import javax.ws.rs.QueryParam;

import io.smallrye.mutiny.Multi;

import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.TransactionService;

import javax.inject.Inject;
import org.jboss.logging.Logger;

@GraphQLApi
public class TransactionGraphQLAPI {
    private static final Logger LOG = Logger.getLogger(TransactionGraphQLAPI.class);

    private static final BroadcastProcessor<Transaction> processor = BroadcastProcessor.create(); 

    @Inject
    TransactionService transactionService;

    @Query
    @Description("Get a transaction given its hash")
    public Transaction getTransaction(String hash) throws IOException {
        return transactionService.getTransactionByHash(hash);
    }

    @Query
    @Description("Return the transaction (optional) from, (optional) to an account. Return all transactions if neither from nor to is specified.")
    public List<Transaction> getTransactions(@QueryParam("from") String from, @QueryParam("to") String to)
            throws IOException {
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

    @Query
    @Description("Return all transactions for a given account")
    public List<Transaction> getAccount(String account) throws IOException {
        return transactionService.getAllTransactionsByAccount(account);
    }

    @Mutation
    @Description("Add a new transaction")
    public Transaction addTransaction(Transaction transaction) throws IOException {
        LOG.info("=========");
        LOG.info(transaction);
        transactionService.index(transaction);
        return transactionService.getTransactionByHash(transaction.hash);
    }

    @Subscription
    public Multi<Transaction> transactionCreated(){
        return processor; 
    }

    @Mutation
    @Description("Remove a transaction")
    public Transaction deleteTransaction(String hash) throws IOException {
        transactionService.delete(hash);
        return transactionService.getTransactionByHash(hash);
    }
}
