package com.syntifi.ori.model;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * {@link Account} model tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class AccountTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNullToken() {
        var account = new Account();
        account.setChain(null);
        account.setHash("hash");
        account.setPublicKey("key");
        account.setLabel("from");
        Set<ConstraintViolation<Account>> constraintViolations = validator.validate(account);
        Assertions.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testNotNullToken() {
        var account = new Account();
        var chain = new Chain();
        chain.setName("chain");
        account.setChain(chain);
        account.setHash("hash");
        account.setPublicKey("key");
        account.setLabel("from");
        Set<ConstraintViolation<Account>> constraintViolations = validator.validate(account);
        Assertions.assertEquals(0, constraintViolations.size());
    }
}
