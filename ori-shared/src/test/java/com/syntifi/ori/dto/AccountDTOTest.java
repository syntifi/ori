package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vertx.java.core.json.JsonObject;

public class AccountDTOTest {

    @Test
    public void testSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        var account = new JsonObject();
        account.putString("tokenSymbol", "TKN");
        account.putString("hash", "from");
        account.putString("publicKey", "key");
        account.putString("label", "from");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(account.toString(), AccountDTO.class));
    }
}

