package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

public class TransactionDTOTest {

    @Test
    public void testSerializeWrongDateType() {
        ObjectMapper mapper = new ObjectMapper();
        var transaction = new JsonObject();
        transaction.put("timeStamp", "2099-08-05");
        transaction.put("hash", "mockTransaction");
        transaction.put("from", "from");
        transaction.put("to", "to");
        transaction.put("amount", 1234);
        transaction.put("blockHash", "block");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(transaction.toString(), TransactionDTO.class));
        Assertions.assertTrue(e.getMessage().contains("Date"));
    }
}
