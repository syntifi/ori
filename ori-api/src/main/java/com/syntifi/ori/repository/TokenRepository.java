package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Token;

@ApplicationScoped
public class TokenRepository implements Repository<Token> {

    public Token findBySymbol(String symbol) throws ORIException {
        List<Token> result = list("symbol", symbol);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean existsAlready(Token token) {
        return findBySymbol(token.getSymbol()) != null;
    }
}
