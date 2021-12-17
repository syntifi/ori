package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Block;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class BlockRepository implements PanacheRepository<Block>, Repository<Block> {

    public Block findByHash(String hash) throws ORIException {
        Block result = find("hash", hash).firstResult();
        if (result == null) {
            throw new ORIException("Block hash " + hash + " not found!", 404);
        }
        return result;
    }
}
