package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

public class AccountDTOTest {

    @Test
    public void testSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        var account = new JsonObject();
        account.put("tokenSymbol", "TKN");
        account.put("hash", "from");
        account.put("publicKey", "key");
        account.put("label", "from");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(account.toString(), AccountDTO.class));
    }
}

