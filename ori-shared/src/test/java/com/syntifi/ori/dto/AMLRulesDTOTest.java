package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AMLRulesDTOTest {
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerializer() {
        ObjectNode rules = mapper.createObjectNode();
        rules.put("structuringOverTimeScore", 0.76876);
        rules.put("unusualOutgoingVolumeScore", 0.1789);
        rules.put("unusualBehaviorScore", 0.56023);
        rules.put("flowThroughScore", 0.76876);
        Assertions.assertDoesNotThrow(() -> mapper.readValue(rules.toString(), AMLRulesDTO.class));
    }
}

