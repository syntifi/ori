package com.syntifi.ori.repository;

import com.syntifi.ori.model.Chain;

import javax.enterprise.context.ApplicationScoped;

/**
 * Ori Repository for {@link Chain} model
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.2.0
 */
@ApplicationScoped
public class ChainRepository implements OriRepository<Chain> {
    /**
     * Returns the chain given its name
     *
     * @param name
     * @return Chain for the given name
     */
    public Chain findByName(String name) {
        return find("name", name).singleResult();
    }

    /**
     * counts the number of chains stored in the DB given its name
     *
     * @param name
     * @return value of the query count
     */
    public long countByName(String name) {
        return count("name", name);
    }

    /**
     * checks if a chain given by the symbol is present in the DB
     *
     * @param chainName
     * @return Boolean
     */
    public boolean existsAlready(String chainName) {
        //return list("select exists(select 1 from chain where name=:name)",
        //        Parameters.with("name", chainName)).size() > 0;
        return countByName(chainName) > 0;
    }
}
