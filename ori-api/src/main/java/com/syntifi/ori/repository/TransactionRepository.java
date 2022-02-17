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

    /**
     * Find an transctoin given a (token, hash)
     * 
     * @param tokenSymbol
     * @param hash
     * @return
     */
    public Transaction findByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return find("block_token_symbol = ?1 AND hash = ?2", tokenSymbol, hash).singleResult();
    }

    /**
     * return the number of transactions with the given (token, hash) pair
     * 
     * @param tokenSymbol
     * @param hash
     * @return
     */
    public long countByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return count("block_token_symbol = ?1 AND hash = ?2", tokenSymbol, hash);
    }

    /**
     * checks if the transaction with the pair (token, hash) exists already
     * 
     * @param tokenSymbol
     * @param hash
     * @return
     */
    public boolean existsAlready(String tokenSymbol, String hash) {
        return countByTokenSymbolAndHash(tokenSymbol, hash) > 0;
    }

    /**
     * returns all outgoing transaction from the given account
     * 
     * @param tokenSymbol
     * @param account
     * @return
     */
    public List<Transaction> getOutgoingTransactions(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 AND fromaccount_hash = ?2", tokenSymbol, account);
    }

    /**
     * returns all outgoing transaction from the given account over the period
     * between fromDate and toDate
     * 
     * @param tokenSymbol
     * @param account
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<Transaction> getOutgoingTransactions(String tokenSymbol, String account,
            OffsetDateTime fromDate, OffsetDateTime toDate) {
        return list(
                "block_token_symbol = ?1 AND fromaccount_hash = ?2 AND (time_stamp BETWEEN ?3 AND ?4)",
                tokenSymbol, account, fromDate, toDate);
    }

    /**
     * returns all incoming transaction to the given account
     * 
     * @param tokenSymbol
     * @param account
     * @return
     */
    public List<Transaction> getIncomingTransactions(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 AND toaccount_hash = ?2", tokenSymbol, account);
    }

    /**
     * returns all incoming transaction to the given account over the period
     * between fromDate and toDate
     * 
     * @param tokenSymbol
     * @param account
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<Transaction> getIncomingTransactions(String tokenSymbol, String account,
            OffsetDateTime fromDate, OffsetDateTime toDate) {
        return list(
                "block_token_symbol = ?1 AND toaccount_hash = ?2 AND (time_stamp BETWEEN ?3 AND ?4)",
                tokenSymbol, account, fromDate, toDate);
    }

    /**
     * get all transactions from a given account to a given account
     * 
     * @param tokenSymbol
     * @param fromAccount
     * @param toAccount
     * @return
     */
    public List<Transaction> getTransactionsByTokenSymbolAndFromAccountAndToAccount(String tokenSymbol,
            String fromAccount,
            String toAccount) {
        return list("block_token_symbol = ?1 AND fromaccount_hash = ?2 AND toaccount_hash = ?3",
                tokenSymbol, fromAccount, toAccount);
    }

    /**
     * get all transactions from a given account to a given account during the
     * period between fromDate and toDAte
     * 
     * @param tokenSymbol
     * @param fromAccount
     * @param toAccount
     * @param fromDate
     * @param toDate
     * @return
     */
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

    /**
     * get all incoming or outgoing transactions from or to the given account
     * 
     * @param tokenSymbol
     * @param account
     * @return
     */
    public List<Transaction> getTransactionsByTokenSymbolAndAccount(String tokenSymbol, String account) {
        return list("block_token_symbol = ?1 AND (fromaccount_hash = ?2 OR toaccount_hash = ?2)", tokenSymbol, account);
    }

    /**
     * get all incoming or outgoing transactions from or to a given account during
     * the period between fromDate and toDate
     * 
     * @param tokenSymbol
     * @param fromDate
     * @param toDate
     * @param sort
     * @param pageSize
     * @return
     */
    public PanacheQuery<Transaction> getAllTransactionsByTokenSymbolFromDateToDate(String tokenSymbol,
            OffsetDateTime fromDate,
            OffsetDateTime toDate, Sort sort,
            int pageSize) {
        return find("block_token_symbol = ?1 AND time_stamp BETWEEN ?2 AND ?3",
                sort, tokenSymbol, fromDate, toDate)
                        .page(Page.ofSize(pageSize));
    }

    /**
     * get all transactions in a given chain
     * 
     * @param tokenSymbol
     * @return
     */
    public List<Transaction> getAllTransactions(String tokenSymbol) {
        return list("block_token_symbol", Sort.descending("time_stamp"), tokenSymbol);
    }

}
