package com.syntifi.ori.repository;

import com.syntifi.ori.model.Token;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Ori Repository for {@link Token} model
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@ApplicationScoped
public class TokenRepository implements OriRepository<Token> {

    /**
     * Returns the token given its symbol
     *
     *
     * @param chain
     * @param symbol
     * @return
     */
    public Token findByChainAndSymbol(String chain, String symbol) {
        return find("chain_name = ?1 AND symbol = ?2", chain, symbol).singleResult();
    }


    /**
     * Returns all tokens for a given chain
     *
     * @param chain
     * @return
     */
    public PanacheQuery<Token> getAllTokens(String chain) {
        return find("chain_name", Sort.ascending("chain_name", "name"), chain);
    }

    /**
     * counts the number of tokens stored in the DB given its symbol
     *
     *
     * @param chain
     * @param symbol
     * @return
     */
    public long countByChainAndSymbol(String chain, String symbol) {
        return count("chain_name = ?1 AND symbol = ?2", chain, symbol);
    }

    /**
     * checks if a token given by the symbol is present in the DB
     *
     *
     * @param chainName
     * @param tokenSymbol
     * @return
     */
    public boolean existsAlready(String chainName, String tokenSymbol) {
        return countByChainAndSymbol(chainName, tokenSymbol) > 0;
    }
}
