package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Block;

import io.quarkus.panache.common.Sort;

/**
 * Ori Repository for {@link Block} model
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@ApplicationScoped
public class BlockRepository implements OriRepository<Block> {

    /**
     * Find a block given it's PK (token, hash)
     * 
     * @param tokenSymbol
     * @param hash
     * @return
     */
    public Block findByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return find("token_symbol= ?1 and hash = ?2", tokenSymbol, hash).singleResult();
    }

    /**
     * return the number of blocks with the given (token, hash) pair 
     * 
     * @param tokenSymbol
     * @param hash
     * @return
     */
    public long countByTokenSymbolAndHash(String tokenSymbol, String hash) {
        return count("token_symbol= ?1 and hash = ?2", tokenSymbol, hash);
    }

    /**
     * returns the last block in the database for the chain specified by the token
     *  
     * @param tokenSymbol
     * @return
     */
    public Block getLastBlock(String tokenSymbol) {
        return find("token_symbol", Sort.descending("time_stamp"), tokenSymbol).firstResult();
    }

    /**
     * returns all blocks in descendin order for the chain specified by the token
     * 
     * @param tokenSymbol
     * @return
     */
    public List<Block> getBlocks(String tokenSymbol) {
        return list("token_symbol", Sort.descending("time_stamp"), tokenSymbol);
    }

    /**
     * checks if the account with the pair (token, hash) exists already
     * 
     * @param tokenSymbol
     * @param blockHash
     * @return
     */
    public boolean existsAlready(String tokenSymbol, String blockHash) {
        return countByTokenSymbolAndHash(tokenSymbol, blockHash) > 0;
    }

    /**
     * checks if any block for the given token is stored in the DB 
     * 
     * @param tokenSymbol
     * @return
     */
    public boolean existsAnyByToken(String tokenSymbol) {
        return count("token_symbol= ?1", tokenSymbol) > 0;
    }
}
