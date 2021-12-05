package com.syntifi.ori;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syntifi.ori.model.Block;
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
    public void testSerializeWrongDateType() {
        ObjectMapper mapper = new ObjectMapper();
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("parent", "parent");
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05");
        block.put("validator", "validator");
        try {
            mapper.readValue(block.toString(), Block.class);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains("Date"));
        }
    }

    @Test
    public void testSerializeWrongEraType() {
        ObjectMapper mapper = new ObjectMapper();
        var block = new JsonObject();
        block.put("era", "0asdfasfas");
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("parent", "parent");
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        block.put("validator", "validator");
        try {
            mapper.readValue(block.toString(), Block.class);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains("long"));
        }
    }
}