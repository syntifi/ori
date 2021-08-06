package com.syntifi.ori;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.TransactionService;

@QuarkusTest
public class TestTransactionService {
    @Inject
    TransactionService transactionService;

    
    @Test
    public void testGetNonExistingTransaction() {
        try {
            transactionService.getTransactionByHash("testTransaction");
        } catch (IOException e) {
            var exception = transactionService.parseElasticError(e);
            Assertions.assertEquals("Not Found", exception.getMessage());
            Assertions.assertEquals(404, exception.getStatus().getStatusCode());
        }
    }

    @Test
    public void testSerializeWrongDateType() {
        ObjectMapper mapper = new ObjectMapper();
        var transaction = new JsonObject();
        transaction.put("amount", 0F);
        transaction.put("blockHash", "mockBlockTransaction");
        transaction.put("from", "mockFrom");
        transaction.put("to", "mockTo");
        transaction.put("hash", "mockTransaction");
        transaction.put("timeStamp", "2099-08-05");
        try {
            mapper.readValue(transaction.toString(), Transaction.class);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains("Date"));
        }
    }

    @Test
    public void testSerializeWrongEraType() {
        ObjectMapper mapper = new ObjectMapper();
        var transaction = new JsonObject();
        transaction.put("amount", "0asdfsadf");
        transaction.put("blockHash", "mockBlockTransaction");
        transaction.put("from", "mockFrom");
        transaction.put("to", "mockTo");
        transaction.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        try {
            mapper.readValue(transaction.toString(), Transaction.class);
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains("Double"));
        }
    }
}
