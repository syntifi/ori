package com.syntifi.ori.model;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestModelTransaction {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
