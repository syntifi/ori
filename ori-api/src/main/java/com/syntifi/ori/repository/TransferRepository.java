package com.syntifi.ori.repository;

import com.syntifi.ori.model.Transfer;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import java.time.OffsetDateTime;

/**
 * Ori Repository for {@link Transfer} model
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@ApplicationScoped
public class TransferRepository implements OriRepository<Transfer> {

    /**
     * Find a transfer given a (token, hash)
     *
     * @param chainName
     * @param hash
     * @return
     */
    public Transfer findByChainNameAndHash(String chainName, String hash) {
        return find("token.chain.name= ?1 AND hash = ?2", chainName, hash).singleResult();
    }

    /**
     * return the number of transfers with the given (chain, hash) pair
     *
     * @param chainName
     * @param hash
     * @return
     */
    public long countByChainNameAndHash(String chainName, String hash) {
        return count("token.chain.name = ?1 AND hash = ?2", chainName, hash);
    }

    /**
     * checks if the transfer with the pair (chain, hash) exists already
     *
     * @param chainName
     * @param hash
     * @return
     */
    public boolean existsAlready(String chainName, String hash) {
        return countByChainNameAndHash(chainName, hash) > 0;
    }

    /**
     * returns all outgoing transfer from the given account
     *
     * @param chainName
     * @param account
     * @return
     */
    public PanacheQuery<Transfer> getOutgoingTransfers(String chainName, String account) {
        return find("token.chain.name= ?1 AND fromaccount_hash = ?2",
                Sort.descending("time_stamp"), chainName, account);
    }

    /**
     * returns all outgoing transfer from the given account over the period
     * between fromDate and toDate
     *
     * @param account
     * @param fromDate
     * @param toDate
     * @return
     */
    public PanacheQuery<Transfer> getOutgoingTransfers(String chainName, String account,
                                                          OffsetDateTime fromDate, OffsetDateTime toDate) {
        return find(
                "token.chain.name = ?1 AND fromaccount_hash = ?2 AND (time_stamp BETWEEN ?3 AND ?4)",
                Sort.descending("time_stamp"), chainName, account, fromDate, toDate);
    }

    /**
     * returns all incoming transfer to the given account
     *
     * @param chainName
     * @param account
     * @return
     */
    public PanacheQuery<Transfer> getIncomingTransfers(String chainName, String account) {
        return find("token.chain.name = ?1 AND toaccount_hash = ?2",
                Sort.descending("time_stamp"), chainName, account);
    }

    /**
     * returns all incoming transfer to the given account over the period
     * between fromDate and toDate
     *
     * @param account
     * @param fromDate
     * @param toDate
     * @return
     */
    public PanacheQuery<Transfer> getIncomingTransfers(String chainName, String account,
                                                          OffsetDateTime fromDate, OffsetDateTime toDate) {
        return find("token.chain.name = ?1 AND toaccount_hash = ?2 AND (time_stamp BETWEEN ?3 AND ?4)",
                Sort.descending("time_stamp"), chainName, account, fromDate, toDate);
    }

    /**
     * get all transfers from a given account to a given account
     *
     * @param chainName
     * @param fromAccount
     * @param toAccount
     * @return
     */
    public PanacheQuery<Transfer> getTransfersFromAccountAndToAccount(String chainName,
                                                                         String fromAccount,
                                                                         String toAccount) {
        return find("token.chain.name = ?1 AND fromaccount_hash = ?2 AND toaccount_hash = ?3",
                Sort.descending("time_stamp"), chainName, fromAccount, toAccount);
    }

    /**
     * get all transfers from a given account to a given account during the
     * period between fromDate and toDAte
     *
     * @param chainName
     * @param fromAccount
     * @param toAccount
     * @param fromDate
     * @param toDate
     * @return
     */
    public PanacheQuery<Transfer> getTransfersFromAccountAndToAccount(String chainName,
                                                                         String fromAccount,
                                                                         String toAccount,
                                                                         OffsetDateTime fromDate,
                                                                         OffsetDateTime toDate) {
        return find(
                "token.chain.name = ?1 AND fromaccount_hash = ?2 AND toaccount_hash = ?3 AND (time_stamp BETWEEN ?4 AND ?5)",
                Sort.descending("time_stamp"), chainName, fromAccount, toAccount, fromDate, toDate);
    }

    /**
     * get all incoming or outgoing transfers from or to the given account
     *
     * @param chainName
     * @param account
     * @return
     */
    public PanacheQuery<Transfer> getAllTransfersForAccount(String chainName, String account) {
        return find("token.chain.name = ?1 AND (fromaccount_hash = ?2 OR toaccount_hash = ?2)",
                Sort.descending("time_stamp"), chainName, account);
    }

    public PanacheQuery<Transfer> getAllTransfersForAccount(String chainName,
                                                               String account,
                                                               OffsetDateTime fromDate,
                                                               OffsetDateTime toDate) {
        return find("token.chain.name = ?1 AND (fromaccount_hash = ?2 OR toaccount_hash = ?2) AND time_stamp BETWEEN ?3 AND ?4",
                Sort.descending("time_stamp"), chainName, account, fromDate, toDate);
    }


    /**
     * get all transfers in a given chain
     *
     * @param chainName
     * @return
     */
    public PanacheQuery<Transfer> getAllTransfers(String chainName) {
        return find("token.chain.name", Sort.descending("time_stamp"), chainName);
    }

    /**
     * get all transfers in a given chain in the interval between [fromDate, toDate]
     *
     * @param chainName
     * @param fromDate
     * @param toDate
     * @return
     */
    public PanacheQuery<Transfer> getAllTransfers(String chainName,
                                                     OffsetDateTime fromDate,
                                                     OffsetDateTime toDate) {
        return getAllTransfers(chainName, fromDate, toDate, Sort.descending("time_stamp"));
    }

    /**
     * get all incoming or outgoing transfers from or to a given account during
     * the period between fromDate and toDate
     *
     * @param chainName
     * @param fromDate
     * @param toDate
     * @param sort
     * @return
     */
    public PanacheQuery<Transfer> getAllTransfers(String chainName,
                                                     OffsetDateTime fromDate,
                                                     OffsetDateTime toDate, Sort sort) {
        return find("token.chain.name = ?1 AND time_stamp BETWEEN ?2 AND ?3",
                sort, chainName, fromDate, toDate);
    }
}
