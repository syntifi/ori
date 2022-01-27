package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Account;

@ApplicationScoped
public class AccountRepository implements Repository<Account> {

    public Account findByHash(String tokenSymbol, String hash) {
        List<Account> result = list("token_symbol = ?1 and hash = ?2", tokenSymbol, hash);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean existsAlready(String tokenSymbol, Account account) {
        return findByHash(tokenSymbol, account.getHash()) != null;
    }
}
