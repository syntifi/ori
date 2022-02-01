package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Account;

@ApplicationScoped
public class AccountRepository implements Repository<Account> {

    public Account findByHashAndTokenSymbol(String tokenSymbol, String hash) {
        return find("token_symbol = ?1 and hash = ?2", tokenSymbol, hash).singleResult();
    }

    public long countByHashAndTokenSymbol(String tokenSymbol, String hash) {
        return count("token_symbol = ?1 and hash = ?2", tokenSymbol, hash);
    }

    public boolean existsAlready(String tokenSymbol, String hash) {
        return countByHashAndTokenSymbol(tokenSymbol, hash) > 0;
    }
}
