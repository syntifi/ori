package com.syntifi.ori.repository;

import java.time.OffsetDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Transaction;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

/**
 * Ori Repository for {@link Transaction} model
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@ApplicationScoped
public class TransactionRepository implements OriRepository<Transaction> {

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
                "block_token_symbol = ?1 AND fromaccount_hash = ?2 AND (time_stamp BETWEEN ?3 AND ?4)",
                tokenSymbol, account, fromDate, toDate);
    }

    public List<Transaction> getIncomingTransactions(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 AND toaccount_hash = ?2", tokenSymbol, account);
    }

    public List<Transaction> getIncomingTransactions(String tokenSymbol, String account,
            OffsetDateTime fromDate, OffsetDateTime toDate) {
        return list(
                "block_token_symbol = ?1 AND toaccount_hash = ?2 AND (time_stamp BETWEEN ?3 AND ?4)",
                tokenSymbol, account, fromDate, toDate);
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
                "block_token_symbol = ?1 AND fromaccount_hash = ?2 AND toaccount_hash = ?3 AND (time_stamp BETWEEN ?4 AND ?5)",
                tokenSymbol, fromAccount, toAccount, fromDate, toDate);
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
        return find("block_token_symbol = ?1 AND time_stamp BETWEEN ?2 AND ?3",
                sort, tokenSymbol, fromDate, toDate)
                        .page(Page.ofSize(pageSize));
    }

}
