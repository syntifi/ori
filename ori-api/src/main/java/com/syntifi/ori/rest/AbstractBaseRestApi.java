package com.syntifi.ori.rest;

import javax.inject.Inject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.AccountRepository;
import com.syntifi.ori.repository.TokenRepository;

public abstract class AbstractBaseRestApi {

    @Inject
    protected TokenRepository tokenRepository;

    @Inject
    protected AccountRepository accountRepository;

    protected Token getTokenOr404(String symbol) {
        Token token = tokenRepository.findBySymbol(symbol);
        if (token == null) {
            throw new ORIException("Token not found", 404);
        }
        return token;
    }

    protected Account getAccountOr404(String symbol, String hash) {
        Account account = accountRepository.findByHashAndTokenSymbol(symbol, hash);
        if (account == null) {
            throw new ORIException("Account not found", 404);
        }
        return account;
    }
}
