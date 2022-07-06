package com.syntifi.ori.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * {@link Chain} model tests
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 *
 * @since 0.2.0
 */
public class ChainTest {
    @Inject
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNullName() {
        var chain = new Chain();
        chain.setName(null);
        Set<ConstraintViolation<Chain>> constraintViolations = validator.validate(chain);
        Assertions.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testChain() {
        var chain = new Chain();
        chain.setName("name");
        Set<ConstraintViolation<Chain>> constraintViolations = validator.validate(chain);
        Assertions.assertEquals(0, constraintViolations.size());
    }
}
