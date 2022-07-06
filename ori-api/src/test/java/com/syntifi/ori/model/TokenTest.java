package com.syntifi.ori.model;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * {@link Token} model tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class TokenTest {
    @Inject
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNullSymbol() {
        var token = new Token();
        var chain = new Chain();
        chain.setName("chain");
        token.setName("name");
        token.setChain(chain);
        token.setSymbol(null);
        Set<ConstraintViolation<Token>> constraintViolations = validator.validate(token);
        Assertions.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testNullName() {
        var token = new Token();
        var chain = new Chain();
        chain.setName("chain");
        token.setName(null);
        token.setChain(chain);
        token.setSymbol("SYM");
        Set<ConstraintViolation<Token>> constraintViolations = validator.validate(token);
        Assertions.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testNullChain() {
        var token = new Token();
        token.setName("name");
        token.setChain(null);
        token.setSymbol("SYM");
        Set<ConstraintViolation<Token>> constraintViolations = validator.validate(token);
        Assertions.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testToken() {
        var token = new Token();
        var chain = new Chain();
        chain.setName("chain");
        token.setName("name");
        token.setChain(chain);
        token.setSymbol("SYM");
        Set<ConstraintViolation<Token>> constraintViolations = validator.validate(token);
        Assertions.assertEquals(0, constraintViolations.size());
    }

}
