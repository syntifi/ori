package com.syntifi.ori.model;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

public class TestModelTransaction {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testSerializeWrongDateType() {
        ObjectMapper mapper = new ObjectMapper();
        var token = new JsonObject();
        token.put("symbol", "SYM");
        token.put("name", "name");
        token.put("protocol", "protocol");
        var from = new JsonObject();
        from.put("token", token);
        from.put("hash", "from");
        from.put("publicKey", "key");
        from.put("label", "from");
        var to = new JsonObject();
        from.put("token", token);
        from.put("hash", "to");
        from.put("publicKey", "key");
        from.put("label", "to");
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        block.put("validator", "validator");
        block.put("parent", null);
        var transaction = new JsonObject();
        transaction.put("timeStamp", "2099-08-05");
        transaction.put("hash", "mockTransaction");
        transaction.put("from", from);
        transaction.put("to", to);
        transaction.put("amount", 1234);
        transaction.put("block", block.toString());
        var e = Assertions.assertThrows(Exception.class, () -> mapper.readValue(transaction.toString(), Transaction.class));
        Assertions.assertTrue(e.getMessage().contains("Date"));
    }

    @Test
    public void testNotNull() {
        var token = new Token();
        var from = new Account();
        var to = new Account();
        var block = new Block();
        var transaction = new Transaction();
        token.setSymbol("SYM");
        token.setName("name");
        token.setProtocol("protocol");
        from.setToken(token);
        from.setHash("from");
        from.setPublicKey("key");
        from.setLabel("from");
        to.setToken(token);
        to.setHash("to");
        to.setPublicKey("key");
        to.setLabel("from");
        block.setEra(0L);
        block.setHash("mockBlock");
        block.setHeight(0L);
        block.setRoot("root");
        block.setTimeStamp(new Date());
        block.setValidator("validator");
        block.setParent(null);
        transaction.setTimeStamp(new Date());
        transaction.setHash("mockTransaction");
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setAmount(1234.);
        transaction.setBlock(block);
        Set<ConstraintViolation<Transaction>> constraintViolations = validator.validate(transaction);
        Assertions.assertEquals(0, constraintViolations.size());
    }
}
