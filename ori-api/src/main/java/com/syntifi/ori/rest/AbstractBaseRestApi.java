package com.syntifi.ori.rest;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

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
        try {
            return tokenRepository.findBySymbol(symbol);
        } catch (NoResultException e) {
            throw new ORIException(symbol + " not found", 404);
        } catch (NonUniqueResultException e) {
            throw new ORIException(symbol + " not unique", 500);
        }
    }

    protected Account getAccountOr404(String symbol, String hash) {
        try {
            return accountRepository.findByTokenSymbolAndHash(symbol, hash);
        } catch (NoResultException e) {
            throw new ORIException(hash + " not found", 404);
        } catch (NonUniqueResultException e) {
            throw new ORIException(hash + " not unique", 500);
        }
    }
}
