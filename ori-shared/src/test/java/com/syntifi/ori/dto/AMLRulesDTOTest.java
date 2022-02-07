package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vertx.java.core.json.JsonObject;

public class AMLRulesDTOTest {

    @Test
    public void testSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        var rules = new JsonObject();
        rules.putNumber("structuringOverTimeScore", 0.76876);
        rules.putNumber("unusualOutgoingVolumeScore", 0.1789);
        rules.putNumber("unusualBehaviorScore", 0.56023);
        rules.putNumber("flowThroughScore", 0.76876);
        Assertions.assertDoesNotThrow(() -> mapper.readValue(rules.toString(), AMLRulesDTO.class));
    }
}

