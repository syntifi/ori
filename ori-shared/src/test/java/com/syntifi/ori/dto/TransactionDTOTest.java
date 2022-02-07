package com.syntifi.ori.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vertx.java.core.json.JsonObject;

public class TransactionDTOTest {

    @Test
    public void testSerializeWrongDateType() {
        ObjectMapper mapper = new ObjectMapper();
        var transaction = new JsonObject();
        transaction.putString("timeStamp", "2099-08-05");
        transaction.putString("hash", "mockTransaction");
        transaction.putString("from", "from");
        transaction.putString("to", "to");
        transaction.putNumber("amount", 1234);
        transaction.putString("blockHash", "block");
        var e = Assertions.assertThrows(Exception.class,
                () -> mapper.readValue(transaction.toString(), TransactionDTO.class));
        Assertions.assertTrue(e.getMessage().contains("Date"));
    }
}
