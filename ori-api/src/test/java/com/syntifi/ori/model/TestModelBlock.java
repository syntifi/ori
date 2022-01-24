package com.syntifi.ori.model;

import java.util.Date;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestModelBlock {
    @Inject
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testNullParent() {
        var block = new Block();
        var token = new Token();
        block.setEra(0L);
        block.setHash("mockBlock0");
        block.setHeight(0L);
        block.setRoot("root");
        block.setTimeStamp(new Date());
        block.setValidator("validator");
        block.setParent(null);
        block.setToken(token);
        Set<ConstraintViolation<Block>> constraintViolations = validator.validate(block);
        Assertions.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testNotNullParent() {
        var parent = new Block();
        var child = new Block();
        var token = new Token();
        parent.setEra(0L);
        parent.setHash("mockBlock0");
        parent.setHeight(0L);
        parent.setRoot("root");
        parent.setTimeStamp(new Date());
        parent.setValidator("validator");
        parent.setParent(null);
        parent.setToken(token);
        child.setEra(0L);
        child.setHash("mockBlock1");
        child.setHeight(1L);
        child.setRoot("root");
        child.setTimeStamp(new Date());
        child.setValidator("validator");
        child.setParent(parent);
        child.setToken(token);
        Set<ConstraintViolation<Block>> constraintViolations = validator.validate(child);
        Assertions.assertEquals(0, constraintViolations.size());
    }
}
