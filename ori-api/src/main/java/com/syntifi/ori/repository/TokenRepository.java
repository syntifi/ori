package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Token;

/**
 * Ori Repository for {@link Token} model
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@ApplicationScoped
public class TokenRepository implements OriRepository<Token> {

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
