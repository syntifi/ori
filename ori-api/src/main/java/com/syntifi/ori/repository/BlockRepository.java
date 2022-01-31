package com.syntifi.ori.repository;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Block;

import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class BlockRepository implements Repository<Block> {

    private Optional<Block> queryBySymbolAndHash(String tokenSymbol, String blockHash) {
        return find("token_symbol= ?1 and hash = ?2", tokenSymbol, blockHash).singleResultOptional();
    }

    public Block findByHash(String tokenSymbol, String blockHash) {
        Optional<Block> block = queryBySymbolAndHash(tokenSymbol, blockHash);
        return block.isPresent() ? block.get() : null;
    }

    public boolean existsAlready(String tokenSymbol, String blockHash) {
        return queryBySymbolAndHash(tokenSymbol, blockHash).isPresent();
    }

    public Block getLastBlock(String tokenSymbol) {
        List<Block> blocks = list("token_symbol", Sort.descending("time_stamp"), tokenSymbol);
        return blocks.isEmpty() ? null : blocks.get(0);
    }

    public List<Block> getBlocks(String tokenSymbol) {
        return list("token_symbol", Sort.descending("time_stamp"), tokenSymbol);
    }

    public boolean existsAnyByToken(String tokenSymbol) {
        return count("token_symbol= ?1", tokenSymbol) > 0;
    }
}
