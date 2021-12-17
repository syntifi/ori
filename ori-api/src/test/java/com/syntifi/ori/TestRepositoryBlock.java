package com.syntifi.ori;

import java.util.Date;

import javax.inject.Inject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.repository.BlockRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestRepositoryBlock {
    @Inject
    BlockRepository blockRepository;

    @Test
    public void testGetNonExistingBlock() {
        var e = Assertions.assertThrowsExactly(ORIException.class, () -> blockRepository.findByHash("testBlock"));
        Assertions.assertEquals("Block hash testBlock not found!", e.getMessage());
        Assertions.assertEquals(404, e.getStatus().getStatusCode());
    }

    @Test
    public void testBlockCheck() {
        var block = new Block();
        block.setEra(0L);
        block.setHash("hash");
        block.setHeight(0L);
        block.setParent(null);
        block.setRoot("root");
        block.setTimeStamp(new Date());
        var e = Assertions.assertThrowsExactly(ORIException.class, () -> blockRepository.check(block));
        Assertions.assertEquals("token must not be null", e.getMessage());
        Assertions.assertEquals(400, e.getStatus().getStatusCode());
    }

}