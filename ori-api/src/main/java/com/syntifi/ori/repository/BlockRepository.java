package com.syntifi.ori.repository;

import com.syntifi.ori.model.Block;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Ori Repository for {@link Block} model
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@ApplicationScoped
public class BlockRepository implements OriRepository<Block> {

    /**
     * Find a block given it's PK (chain, hash)
     *
     * @paramc chainName
     * @param hash
     * @return
     */
    public Block findByChainNameAndHash(String chainName, String hash) {
        return find("chain_name= ?1 and hash = ?2", chainName, hash).singleResult();
    }

    /**
     * return the number of blocks with the given (chain, hash) pair
     *
     * @param chainName
     * @param hash
     * @return
     */
    public long countByChainNameAndHash(String chainName, String hash) {
        return count("chain_name= ?1 and hash = ?2", chainName, hash);
    }

    /**
     * returns the last block in the database for the chain specified by the chain
     *
     * @param chainName
     * @return
     */
    public Block getLastBlock(String chainName) {
        return find("chain_name", Sort.descending("time_stamp"), chainName).firstResult();
    }

    /**
     * returns all blocks in descendin order for the chain specified by the chain
     *
     * @param chainName
     * @return
     */
    public List<Block> getBlocks(String chainName) {
        return list("chain_name", Sort.descending("time_stamp"), chainName);
    }

    /**
     * checks if the account with the pair (chain, hash) exists already
     *
     * @param chainName
     * @param blockHash
     * @return
     */
    public boolean existsAlready(String chainName, String blockHash) {
        return countByChainNameAndHash(chainName, blockHash) > 0;
    }

    /**
     * checks if any block for the given chain is stored in the DB
     *
     * @param chainName
     * @return
     */
    public boolean existsAnyByChain(String chainName) {
        return count("chain_name= ?1", chainName) > 0;
    }
}
