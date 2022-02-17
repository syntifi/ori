package com.syntifi.ori.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.TransactionRepository;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

/**
 * Service for Transaction business logic
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Singleton
public class TransactionService {

    private int maxGraphLength = ConfigProvider.getConfig().getValue("ori.aml.max-trace-coin-length", int.class);
    private static final String TIMESTAMP_FIELD = "timeStamp";

    @Inject
    TransactionRepository transactionRepository;

    public List<Transaction> reverseGraphWalk(String tokenSymbol, String account, OffsetDateTime fromDate,
            OffsetDateTime toDate) {
        int size = 100;
        PanacheQuery<Transaction> allTransactions = transactionRepository.getAllTransactionsByTokenSymbolFromDateToDate(
                tokenSymbol, fromDate, toDate,
                Sort.descending(TIMESTAMP_FIELD), size);
        Set<String> nodes = new HashSet<>();
        nodes.add(account);
        List<Transaction> graph = new ArrayList<>();
        List<Transaction> transactions = allTransactions.firstPage().list();
        while (graph.size() <= maxGraphLength) {
            for (Transaction transaction : transactions) {
                if (transaction.getToAccount() != null && nodes.contains(transaction.getToAccount().getHash())) {
                    nodes.add(transaction.getFromAccount().getHash());
                    graph.add(transaction);
                }
            }
            if (allTransactions.hasNextPage()) {
                transactions = allTransactions.nextPage().list();
            } else {
                break;
            }
        }
        return graph;
    }

    public List<Transaction> forwardGraphWalk(String tokenSymbol, String account, OffsetDateTime fromDate,
            OffsetDateTime toDate) {
        int size = 100;
        PanacheQuery<Transaction> allTransactions = transactionRepository.getAllTransactionsByTokenSymbolFromDateToDate(
                tokenSymbol, fromDate, toDate,
                Sort.ascending(TIMESTAMP_FIELD), size);
        Set<String> nodes = new HashSet<>();
        nodes.add(account);
        List<Transaction> graph = new ArrayList<>();
        List<Transaction> transactions = allTransactions.firstPage().list();
        while (graph.size() <= maxGraphLength) {
            for (Transaction transaction : transactions) {
                if (transaction.getToAccount() != null && nodes.contains(transaction.getFromAccount().getHash())) {
                    nodes.add(transaction.getToAccount().getHash());
                    graph.add(transaction);
                }
            }
            if (allTransactions.hasNextPage()) {
                transactions = allTransactions.nextPage().list();
            } else {
                break;
            }
        }
        return graph;
    }
}
