package com.syntifi.ori.repository;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Transaction;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class TransactionRepository implements Repository<Transaction> {

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ");
    private int maxGraphLength = ConfigProvider.getConfig().getValue("ori.aml.max-trace-coin-length", int.class);
    private static final String TIMESTAMP_FIELD = "timeStamp";

    public Transaction findByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return find("block_token_symbol = ?1 and hash = ?2", tokenSymbol, hash).singleResult();
    }

    public long countByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return count("block_token_symbol = ?1 and hash = ?2", tokenSymbol, hash);
    }

    public boolean existsAlready(String tokenSymbol, String hash) {
        return countByTokenSymbolAndHash(tokenSymbol, hash) > 0;
    }

    public List<Transaction> getOutgoingTransactions(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 and fromaccount_hash = ?2", tokenSymbol, account);
    }

    public List<Transaction> getOutgoingTransactions(String tokenSymbol, String account,
            OffsetDateTime fromDate, OffsetDateTime toDate) {
        return list("block_token_symbol = ?1 and fromaccount_hash = ?2 and time_stamp >= ?3 and time_stamp <= ?4",
                tokenSymbol, account, fromDate.format(formatter), toDate.format(formatter));
    }

    public List<Transaction> getIncomingTransactions(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 and toaccount_hash = ?2", tokenSymbol, account);
    }

    public List<Transaction> getIncomingTransactions(String tokenSymbol, String account,
            OffsetDateTime fromDate, OffsetDateTime toDate) {
        return list("block_token_symbol = ?1 and toaccount_hash = ?2 and time_stamp >= ?3 and time_stamp <= ?4",
                tokenSymbol, account, fromDate.format(formatter), toDate.format(formatter));
    }

    public List<Transaction> getTransactionsByTokenSymbolAndFromAccountAndToAccount(String tokenSymbol,
            String fromAccount,
            String toAccount) {
        return list("block_token_symbol = ?1 and and fromaccount_hash = ?2 and toaccount_hash = ?3",
                tokenSymbol, fromAccount, toAccount);
    }

    public List<Transaction> getTransactionsByTokenSymbolAndFromAccountAndToAccountBetweenTimeStamps(String tokenSymbol,
            String fromAccount,
            String toAccount,
            OffsetDateTime fromDate, OffsetDateTime toDate) {
        return list(
                "block_token_symbol = ?1 and and fromaccount_hash = ?2 and toaccount_hash = ?3 and time_stamp >= ?3 and time_stamp <= ?4",
                tokenSymbol, fromAccount, toAccount, fromDate.format(formatter), toDate.format(formatter));
    }

    public List<Transaction> getTransactionsByTokenSymbolAndAccount(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 and fromaccount_hash = ?2 or toaccount_hash = ?2", tokenSymbol, account);

    }

    public List<Transaction> getTransactions(String tokenSymbol) {
        return list("token_symbol", Sort.descending("time_stamp"), tokenSymbol);
    }

    public List<Transaction> getAllTransactionsFromDateToDate(OffsetDateTime fromDate, OffsetDateTime toDate, Sort sort,
            int pageIndex, int pageSize) {
        return find("timeStamp >= ?1 and timeStamp <= ?2", sort, fromDate, toDate)
                .page(pageIndex, pageSize)
                .list();
    }

    public List<Transaction> reverseGraphWalk(String account, OffsetDateTime fromDate, OffsetDateTime toDate) {
        int page = 0;
        int size = 100;
        List<Transaction> transactions = getAllTransactionsFromDateToDate(fromDate, toDate,
                Sort.descending(TIMESTAMP_FIELD), page, size);
        Set<String> nodes = new HashSet<>();
        nodes.add(account);
        List<Transaction> graph = new ArrayList<>();
        while (graph.size() <= maxGraphLength && !transactions.isEmpty()) {
            page = page + 1;
            for (Transaction transaction : transactions) {
                if (transaction.getToAccount() != null && nodes.contains(transaction.getToAccount().getHash())) {
                    nodes.add(transaction.getFromAccount().getHash());
                    graph.add(transaction);
                }
            }
            transactions = getAllTransactionsFromDateToDate(fromDate, toDate,
                    Sort.descending(TIMESTAMP_FIELD), page, size);
        }
        return graph;
    }

    public List<Transaction> forwardGraphWalk(String account, OffsetDateTime fromDate, OffsetDateTime toDate) {
        int page = 0;
        int size = 100;
        List<Transaction> transactions = getAllTransactionsFromDateToDate(fromDate, toDate,
                Sort.ascending(TIMESTAMP_FIELD), page, size);
        Set<String> nodes = new HashSet<>();
        nodes.add(account);
        List<Transaction> graph = new ArrayList<>();
        while (graph.size() <= maxGraphLength && !transactions.isEmpty()) {
            page = page + 1;
            for (Transaction transaction : transactions) {
                if (transaction.getToAccount() != null && nodes.contains(transaction.getFromAccount().getHash())) {
                    nodes.add(transaction.getToAccount().getHash());
                    graph.add(transaction);
                }
            }
            transactions = getAllTransactionsFromDateToDate(fromDate, toDate,
                    Sort.descending(TIMESTAMP_FIELD), page, size);
        }
        return graph;
    }
}
