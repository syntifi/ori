package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vertx.java.core.json.JsonObject;

public class TokenDTOTest {
    @Test
    public void testSerialize() {
        ObjectMapper mapper = new ObjectMapper();
        var token = new JsonObject();
        token.putString("symbol", "SYM");
        token.putString("name", "name");
        token.putString("protocol", "protocol");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(token.toString(), TokenDTO.class));
    } 
    
}
