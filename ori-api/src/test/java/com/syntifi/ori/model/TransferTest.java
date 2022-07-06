package com.syntifi.ori.model;

import java.time.OffsetDateTime;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * {@link Transfer} model tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class TransferTest {
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
        var transfer = new Transfer();
        var chain = new Chain();
        chain.setName("chain");
        token.setSymbol("SYM");
        token.setName("name");
        token.setChain(chain);
        from.setHash("from");
        from.setPublicKey("key");
        from.setLabel("from");
        to.setHash("to");
        to.setPublicKey("key");
        to.setLabel("from");
        block.setEra(0L);
        block.setHash("mockBlock");
        block.setHeight(0L);
        block.setRoot("root");
        block.setTimeStamp(OffsetDateTime.now());
        block.setValidator("validator");
        block.setParent(null);
        transfer.setTimeStamp(OffsetDateTime.now());
        transfer.setHash("mockTransfer");
        transfer.setToken(token);
        transfer.setFromAccount(from);
        transfer.setToAccount(to);
        transfer.setAmount(1234.);
        transfer.setBlock(block);
        Set<ConstraintViolation<Transfer>> constraintViolations = validator.validate(transfer);
        Assertions.assertEquals(0, constraintViolations.size());
    }
}
