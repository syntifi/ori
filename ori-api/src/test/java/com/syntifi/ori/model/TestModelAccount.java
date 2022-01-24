package com.syntifi.ori.model;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestModelAccount {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNullToken() {
        var account = new Account();
        account.setToken(null);
        account.setHash("hash");
        account.setPublicKey("key");
        account.setLabel("from");
        Set<ConstraintViolation<Account>> constraintViolations = validator.validate(account);
        Assertions.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testNotNullToken() {
        var account = new Account();
        var token = new Token();
        token.setSymbol("symbol");
        token.setName("name");
        token.setProtocol("protocol");
        account.setToken(token);
        account.setHash("hash");
        account.setPublicKey("key");
        account.setLabel("from");
        Set<ConstraintViolation<Account>> constraintViolations = validator.validate(account);
        Assertions.assertEquals(0, constraintViolations.size());
    }
}
