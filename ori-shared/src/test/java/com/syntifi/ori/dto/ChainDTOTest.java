package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link ChainDTO} model tests
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 *
 * @since 0.2.0
 */
public class ChainDTOTest {
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerializer() {
        ObjectNode chain = mapper.createObjectNode();
        chain.put("name", "chain");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(chain.toString(), ChainDTO.class));
    }
}
