package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Account;

@ApplicationScoped
public class AccountRepository implements Repository<Account> {

    public Account findByHash(String hash) {
        List<Account> result = list("hash", hash);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean existsAlready(Account account) {
        return findByHash(account.getHash()) != null;
    }
}
