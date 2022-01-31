package com.syntifi.ori.repository;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Account;

@ApplicationScoped
public class AccountRepository implements Repository<Account> {

    private Optional<Account> query(String tokenSymbol, String hash) {
        return find("token_symbol = ?1 and hash = ?2", tokenSymbol, hash).singleResultOptional();
    }

    public Account findByHashAndTokenSymbol(String tokenSymbol, String hash) {
        Optional<Account> account = query(tokenSymbol, hash);
        return account.isPresent() ? account.get() : null;
    }

    public boolean existsAlready(String tokenSymbol, Account account) {
        return query(tokenSymbol, account.getHash()).isPresent();
    }
}
