package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vertx.java.core.json.JsonObject;

public class BlockDTOTest {

    @Test
    public void testSerializeWrongDateType() {
        ObjectMapper mapper = new ObjectMapper();
        var block = new JsonObject();
        block.putNumber("era", 0);
        block.putString("hash", "mockBlock");
        block.putNumber("height", 0);
        block.putString("root", "root");
        block.putString("timeStamp", "2099-08-05");
        block.putString("validator", "validator");
        block.putString("parent", null);
        block.putString("tokenSymbol", "TKN");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(block.toString(), BlockDTO.class));
        Assertions.assertTrue(e.getMessage().contains("Date"));
    }

    @Test
    public void testSerializeWrongEraType() {
        ObjectMapper mapper = new ObjectMapper();
        var block = new JsonObject();
        block.putString("era", "0asdfasfas");
        block.putString("hash", "mockBlock");
        block.putNumber("height", 0);
        block.putString("root", "root");
        block.putString("timeStamp", "2099-08-05T00:00:00.000");
        block.putString("validator", "validator");
        block.putString("parent", null);
        block.putString("token", "TKN");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(block.toString(), BlockDTO.class));
        Assertions.assertTrue(e.getMessage().contains("long"));
    }

}
