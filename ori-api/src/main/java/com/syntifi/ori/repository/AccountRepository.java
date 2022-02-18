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
     * Find an account given it's PK (token, hash)
     * 
     * @param tokenSymbol
     * @param hash
     * @return
     */
    public Account findByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return find("token_symbol = ?1 and hash = ?2", tokenSymbol, hash).singleResult();
    }

    /**
     * return the number of accounts with the given (token, hash) pair 
     * 
     * @param tokenSymbol
     * @param hash
     * @return
     */
    public long countByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return count("token_symbol = ?1 and hash = ?2", tokenSymbol, hash);
    }

    /**
     * checks if the account with the pair (token, hash) exists already
     *  
     * @param tokenSymbol
     * @param hash
     * @return
     */
    public boolean existsAlready(String tokenSymbol, String hash) {
        return countByTokenSymbolAndHash(tokenSymbol, hash) > 0;
    }
}
