package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Account;

/**
 * Ori Repository for {@link Account} model
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@ApplicationScoped
public class AccountRepository implements OriRepository<Account> {

    /**
     * Find an account given it's PK (chain, hash)
     * 
     * @param chainName
     * @param hash
     * @return
     */
    public Account findByChainNameAndHash(String chainName, String hash) {
        return find("chain_name= ?1 and hash = ?2", chainName, hash).singleResult();
    }

    /**
     * return the number of accounts with the given (chain, hash) pair
     * 
     * @param chainName
     * @param hash
     * @return
     */
    public long countByChainNameAndHash(String chainName, String hash) {
        return count("chain_name= ?1 and hash = ?2", chainName, hash);
    }

    /**
     * checks if the account with the pair (token, hash) exists already
     *  
     * @param chainName
     * @param hash
     * @return
     */
    public boolean existsAlready(String chainName, String hash) {
        return countByChainNameAndHash(chainName, hash) > 0;
    }
}
