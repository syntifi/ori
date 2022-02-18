package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenDTOTest {
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialize() {
        ObjectNode token = mapper.createObjectNode();
        token.put("symbol", "SYM");
        token.put("name", "name");
        token.put("protocol", "protocol");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(token.toString(), TokenDTO.class));
    }

}
