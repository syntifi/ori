package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountDTOTest {

    @Test
    public void testSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        var account = mapper.createObjectNode();
        account.put("tokenSymbol", "TKN");
        account.put("hash", "from");
        account.put("publicKey", "key");
        account.put("label", "from");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(account.toString(), AccountDTO.class));
    }
}
