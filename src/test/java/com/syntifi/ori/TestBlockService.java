package com.syntifi.ori;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import javax.inject.Inject;

import com.syntifi.ori.service.BlockService;

@QuarkusTest
public class TestBlockService {
    @Inject
    BlockService blockService;

    
    @Test
    public void testGetNonExistingBlock() {
        try {
            blockService.getBlockByHash("testBlock");
        } catch (IOException e) {
            var exception = blockService.parseElasticError(e);
            Assertions.assertEquals("Not Found", exception.getMessage());
            Assertions.assertEquals(404, exception.getStatus().getStatusCode());
        }
    }

    @Test
    public void testSerializeWrongBlock() {
        try {
            blockService.getBlockByHash("testBlock");
        } catch (IOException e) {
            var exception = blockService.parseElasticError(e);
            Assertions.assertEquals("Not Found", exception.getMessage());
            Assertions.assertEquals(404, exception.getStatus().getStatusCode());
        }
    }


}