package com.syntifi.ori.repository;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Transaction;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class TransactionRepository implements Repository<Transaction> {
    static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    static final String dateFormatDB = "YYYY-MM-DD'T'HH24:MI:SS.MS+TZHTZM";
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

    public Transaction findByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return find("block_token_symbol = ?1 AND hash = ?2", tokenSymbol, hash).singleResult();
    }

    public long countByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return count("block_token_symbol = ?1 AND hash = ?2", tokenSymbol, hash);
    }

    public boolean existsAlready(String tokenSymbol, String hash) {
        return countByTokenSymbolAndHash(tokenSymbol, hash) > 0;
    }

    public List<Transaction> getOutgoingTransactions(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 AND fromaccount_hash = ?2", tokenSymbol, account);
    }

    public List<Transaction> getOutgoingTransactions(String tokenSymbol, String account,
            OffsetDateTime fromDate, OffsetDateTime toDate) {
        return list(
                "block_token_symbol = ?1 AND fromaccount_hash = ?2 AND (time_stamp BETWEEN TO_TIMESTAMP(?3, ?5) AND TO_TIMESTAMP(?4, ?5))",
                tokenSymbol, account, fromDate.format(formatter), toDate.format(formatter), dateFormatDB);
    }

    public List<Transaction> getIncomingTransactions(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 AND toaccount_hash = ?2", tokenSymbol, account);
    }

    public List<Transaction> getIncomingTransactions(String tokenSymbol, String account,
            OffsetDateTime fromDate, OffsetDateTime toDate) {
        return list(
                "block_token_symbol = ?1 AND toaccount_hash = ?2 AND (time_stamp BETWEEN TO_TIMESTAMP(?3, ?5) AND TO_TIMESTAMP(?4, ?5))",
                tokenSymbol, account, fromDate.format(formatter), toDate.format(formatter), dateFormatDB);
    }

    public List<Transaction> getTransactionsByTokenSymbolAndFromAccountAndToAccount(String tokenSymbol,
            String fromAccount,
            String toAccount) {
        return list("block_token_symbol = ?1 AND fromaccount_hash = ?2 AND toaccount_hash = ?3",
                tokenSymbol, fromAccount, toAccount);
    }

    public List<Transaction> getTransactionsByTokenSymbolAndFromAccountAndToAccountBetweenTimeStamps(
            String tokenSymbol,
            String fromAccount,
            String toAccount,
            OffsetDateTime fromDate,
            OffsetDateTime toDate) {
        return list(
                "block_token_symbol = ?1 AND fromaccount_hash = ?2 AND toaccount_hash = ?3 AND (time_stamp BETWEEN TO_TIMESTAMP(?4, ?6) AND TO_TIMESTAMP(?5, ?6))",
                tokenSymbol, fromAccount, toAccount, fromDate.format(formatter), toDate.format(formatter),
                dateFormatDB);
    }

    public List<Transaction> getTransactionsByTokenSymbolAndAccount(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 AND (fromaccount_hash = ?2 OR toaccount_hash = ?2)", tokenSymbol, account);
    }

    public List<Transaction> getAllTransactions(String tokenSymbol) {
        return list("block_token_symbol", Sort.descending("time_stamp"), tokenSymbol);
    }

    public PanacheQuery<Transaction> getAllTransactionsByTokenSymbolFromDateToDate(String tokenSymbol,
            OffsetDateTime fromDate,
            OffsetDateTime toDate, Sort sort,
            int pageSize) {
        return find("block_token_symbol = ?1 AND time_stamp BETWEEN TO_TIMESTAMP(?2, ?4) AND TO_TIMESTAMP(?3, ?4)",
                sort, tokenSymbol, fromDate.format(formatter), toDate.format(formatter), dateFormatDB)
                        .page(Page.ofSize(pageSize));
    }

}
