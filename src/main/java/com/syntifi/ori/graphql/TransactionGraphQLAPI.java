package com.syntifi.ori.graphql;

import java.util.List;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Description;
import io.smallrye.graphql.api.Subscription;

import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import javax.ws.rs.QueryParam;

import io.smallrye.mutiny.Multi;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.TransactionService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

@GraphQLApi
public class TransactionGraphQLAPI {

    private static final Logger LOG = Logger.getLogger(TransactionGraphQLAPI.class);
    private static final BroadcastProcessor<Transaction> processor = BroadcastProcessor.create(); 

    @Inject
    TransactionService transactionService;

    @Query
    @Description("Get a transaction given its hash")
    public Transaction getTransaction(String hash) throws ORIException{
        try {
            return transactionService.getTransactionByHash(hash);
        } catch (Exception e) {
            var exception = transactionService.parseElasticError(e);
            LOG.info("==================");
            LOG.error(e.getMessage());

            Matcher m = Pattern.compile("status line \\[HTTP/[0-9.]+ ([0-9]+)").matcher(e.getMessage());
            int code = m.find() ? Integer.valueOf(m.group(1)) : 404;
            m = Pattern.compile("status line \\[HTTP/[0-9.]+ [0-9]+ ([A-Za-z ]+)").matcher(e.getMessage());
            String msg = m.find() ? m.group(1) : "";

            LOG.error(code);
            LOG.error(msg);
            LOG.error(exception.getMessage());
            throw transactionService.parseElasticError(e);
        }
    }

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

    @Query
    @Description("Return all transactions for a given account")
    public List<Transaction> getAccount(String account) throws ORIException {
        try {
            return transactionService.getAllTransactionsByAccount(account);
        } catch (Exception e) {
            throw transactionService.parseElasticError(e);
        }
    }

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

    @Subscription
    public Multi<Transaction> transactionCreated(){
        return processor; 
    }

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
