package com.syntifi.ori.repository;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import com.syntifi.ori.model.Token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestRepositoryBlock {
    @Inject
    BlockRepository blockRepository;

    @Test
    public void testGetNonExistingBlock() {
        var token = new Token();
        token.setName("name");
        token.setProtocol("protocol");
        token.setSymbol("symbol");
        Assertions.assertThrows(NoResultException.class,
                () -> blockRepository.findByHash(token.getSymbol(), "testBlock"));
    }

    /*
     * @Test
     * 
     * @Disabled("Check if this is still a valid test")
     * public void testBlockCheck() {
     * var block = new Block();
     * block.setEra(0L);
     * block.setHash("hash");
     * block.setHeight(0L);
     * block.setParent(null);
     * block.setRoot("root");
     * block.setValidator("validator");
     * block.setTimeStamp(new Date());
     * var e = Assertions.assertThrowsExactly(ORIException.class, () ->
     * blockRepository.check(block));
     * Assertions.assertEquals("token must not be null", e.getMessage());
     * Assertions.assertEquals(400, e.getStatus().getStatusCode());
     * }
     */
}