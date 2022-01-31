package com.syntifi.ori.repository;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Token;

@ApplicationScoped
public class TokenRepository implements Repository<Token> {

    private Optional<Token> query(String symbol) {
        return find("symbol", symbol).singleResultOptional();
    }

    public Token findBySymbol(String symbol) {
        Optional<Token> token = query(symbol);
        return token.isPresent() ? token.get() : null;
    }

    public boolean existsAlready(Token token) {
        return query(token.getSymbol()).isPresent();
    }
}
