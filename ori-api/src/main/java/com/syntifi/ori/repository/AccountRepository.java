package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Account;

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
