package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link TokenDTO} model tests
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 *
 * @since 0.1.0
 */
public class TokenDTOTest {
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialize() {
        ObjectNode token = mapper.createObjectNode();
        token.put("symbol", "SYM");
        token.put("name", "name");
        token.put("chainName", "chain");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(token.toString(), TokenDTO.class));
    }

}
