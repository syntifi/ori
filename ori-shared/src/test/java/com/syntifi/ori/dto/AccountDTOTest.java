package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountDTOTest {
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerializer() {
        ObjectNode account = mapper.createObjectNode();
        account.put("tokenSymbol", "TKN");
        account.put("hash", "from");
        account.put("publicKey", "key");
        account.put("label", "from");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(account.toString(), AccountDTO.class));
    }
}
