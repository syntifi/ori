package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

public class TokenDTOTest {
    @Test
    public void testSerialize() {
        ObjectMapper mapper = new ObjectMapper();
        var token = new JsonObject();
        token.put("symbol", "SYM");
        token.put("name", "name");
        token.put("protocol", "protocol");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(token.toString(), TokenDTO.class));
    } 
    
}
