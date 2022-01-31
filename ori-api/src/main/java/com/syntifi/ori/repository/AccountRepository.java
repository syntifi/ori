package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Account;

@ApplicationScoped
public class AccountRepository implements Repository<Account> {

    public Account findByHash(String tokenSymbol, String hash) {
        return find("token_symbol = ?1 and hash = ?2", tokenSymbol, hash).singleResult();
    }

    public boolean existsAlready(String tokenSymbol, Account account) {
        return findByHash(tokenSymbol, account.getHash()) != null;
    }
}
