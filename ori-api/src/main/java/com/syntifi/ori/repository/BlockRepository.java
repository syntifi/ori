package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Block;

import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class BlockRepository implements Repository<Block> {

    public Block findByHash(String tokenSymbol, String blockHash) {
        return find("token_symbol= ?1 and hash = ?2", tokenSymbol, blockHash).singleResult();
    }

    public long countByHash(String tokenSymbol, String blockHash) {
        return count("token_symbol= ?1 and hash = ?2", tokenSymbol, blockHash);
    }

    public Block getLastBlock(String tokenSymbol) {
        return find("token_symbol", Sort.descending("time_stamp"), tokenSymbol).firstResult();
    }

    public List<Block> getBlocks(String tokenSymbol) {
        return list("token_symbol", Sort.descending("time_stamp"), tokenSymbol);
    }

    public boolean existsAlready(String tokenSymbol, String blockHash) {
        return countByHash(tokenSymbol, blockHash) > 0;
    }

    public boolean existsAnyByToken(String tokenSymbol) {
        return count("token_symbol= ?1", tokenSymbol) > 0;
    }
}
