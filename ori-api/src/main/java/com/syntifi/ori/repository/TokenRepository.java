package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Token;

@ApplicationScoped
public class TokenRepository implements Repository<Token> {

    public Token findBySymbol(String symbol) {
        return find("symbol", symbol).singleResult();
    }

    public long countBySymbol(String symbol) {
        return count("symbol", symbol);
    }

    public boolean existsAlready(String tokenSymbol) {
        return countBySymbol(tokenSymbol) > 0;
    }
}
