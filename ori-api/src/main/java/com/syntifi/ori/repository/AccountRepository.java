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

    public Account findByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return find("token_symbol = ?1 and hash = ?2", tokenSymbol, hash).singleResult();
    }

    public long countByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return count("token_symbol = ?1 and hash = ?2", tokenSymbol, hash);
    }

    public boolean existsAlready(String tokenSymbol, String hash) {
        return countByTokenSymbolAndHash(tokenSymbol, hash) > 0;
    }
}
