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

    /**
     * Returns the token given its symbol
     * 
     * @param symbol
     * @return
     */
    public Token findBySymbol(String symbol) {
        return find("symbol", symbol).singleResult();
    }

    /**
     * counts the number of tokens stored in the DB given its symbol
     * 
     * @param symbol
     * @return
     */
    public long countBySymbol(String symbol) {
        return count("symbol", symbol);
    }

    /**
     * checks if a token given by the symbol is present in the DB
     * 
     * @param tokenSymbol
     * @return
     */
    public boolean existsAlready(String tokenSymbol) {
        return countBySymbol(tokenSymbol) > 0;
    }
}
