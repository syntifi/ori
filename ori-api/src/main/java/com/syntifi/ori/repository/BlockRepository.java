package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;

import org.hibernate.criterion.Order;

@ApplicationScoped
public class BlockRepository implements Repository<Block> {

    public Block findByHash(String hash) {
        List<Block> result = list("hash", hash);
        return result.isEmpty() ? null : result.get(0);
    }

    public Block getLastBlock(Token token) {
        List<Block> result = list("token", Order.desc("timeStamp"), token);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean existsAlready(Block block) {
        return findByHash(block.getHash()) != null;
    }
}
