package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Block;

@ApplicationScoped
public class BlockRepository implements Repository<Block> {

    public Block findByHash(String hash) throws ORIException {
        List<Block> result = list("hash", hash);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean existsAlready(Block block) {
        return findByHash(block.getHash()) != null;
    }
}
