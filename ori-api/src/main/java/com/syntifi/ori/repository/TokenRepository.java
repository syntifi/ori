package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Token;

@ApplicationScoped
public class TokenRepository implements Repository<Token> {

    public Token findBySymbol(String symbol) throws ORIException {
        return find("symbol", symbol).singleResult();
    }

    public long countBySymbol(String symbol) throws ORIException {
        return count("symbol", symbol);
    }

    public boolean existsAlready(Token token) {
        return countBySymbol(token.getSymbol()) > 0;
    }
}
