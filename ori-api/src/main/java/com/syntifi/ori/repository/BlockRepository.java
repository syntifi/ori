package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.syntifi.ori.model.Block;

import io.quarkus.panache.common.Sort;

@ApplicationScoped
@Transactional
public class BlockRepository implements Repository<Block> {

    public Block findByHash(String tokenSymbol, String blockHash) {
        return find("token_symbol= ?1 and hash = ?2", tokenSymbol, blockHash).firstResult();
    }

    public Block getLastBlock(String tokenSymbol) {
        return find("token_symbol", Sort.descending("time_stamp"), tokenSymbol).firstResult();
    }

    public List<Block> getBlocks(String tokenSymbol) {
        return list("token_symbol", Sort.descending("time_stamp"), tokenSymbol);
    }

    public boolean existsAlready(String tokenSymbol, String blockHash) {
        return findByHash(tokenSymbol, blockHash) != null;
    }

    public boolean existsAnyByToken(String tokenSymbol) {
        return find("token_symbol= ?1", tokenSymbol) != null;
    }
}
