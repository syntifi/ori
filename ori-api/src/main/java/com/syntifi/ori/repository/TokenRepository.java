package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Token;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TokenRepository implements PanacheRepository<Token>, Repository<Token> {

    public Token findBySymbol(String symbol) throws ORIException {
        Token result = find("symbol", symbol).firstResult();
            if (result == null) {
            throw new ORIException("Token symbol " + symbol + " not found!", 404);
        }
        return result;
    }
}
