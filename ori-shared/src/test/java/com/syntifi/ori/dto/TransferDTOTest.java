package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransferDTOTest {
    final static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerializeWrongDateType() {
        ObjectNode transaction = mapper.createObjectNode();
        transaction.put("timeStamp", "2099-08-05");
        transaction.put("hash", "mockTransaction");
        transaction.put("from", "from");
        transaction.put("to", "to");
        transaction.put("amount", 1234);
        transaction.put("blockHash", "block");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(transaction.toString(), TransferDTO.class));
        Assertions.assertTrue(e.getMessage().contains("Date"));
    }
}
