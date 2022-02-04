package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

public class BlockDTOTest {

    @Test
    public void testSerializeWrongDateType() {
        ObjectMapper mapper = new ObjectMapper();
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05");
        block.put("validator", "validator");
        block.put("parent", null);
        block.put("tokenSymbol", "TKN");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(block.toString(), BlockDTO.class));
        Assertions.assertTrue(e.getMessage().contains("Date"));
    }

    @Test
    public void testSerializeWrongEraType() {
        ObjectMapper mapper = new ObjectMapper();
        var block = new JsonObject();
        block.put("era", "0asdfasfas");
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000");
        block.put("validator", "validator");
        block.put("parent", null);
        block.put("token", "TKN");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(block.toString(), BlockDTO.class));
        Assertions.assertTrue(e.getMessage().contains("long"));
    }

}
