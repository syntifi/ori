package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlockDTOTest {
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerializeWrongDateType() {
        ObjectNode block = mapper.createObjectNode();
        block.put("era", 0);
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05");
        block.put("validator", "validator");
        block.putNull("parent");
        block.put("tokenSymbol", "TKN");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(block.toString(), BlockDTO.class));
        Assertions.assertTrue(e.getMessage().contains("Date"));
    }

    @Test
    public void testSerializeWrongEraType() {
        ObjectNode block = mapper.createObjectNode();
        block.put("era", "0asdfasfas");
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000");
        block.put("validator", "validator");
        block.putNull("parent");
        block.put("token", "TKN");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(block.toString(), BlockDTO.class));
        Assertions.assertTrue(e.getMessage().contains("long"));
    }

}
