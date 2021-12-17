package com.syntifi.ori.repository;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.syntifi.ori.exception.ORIException;

public interface Repository<T> {

     public default void check(T obj ) throws ORIException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        if (!constraintViolations.isEmpty()) {
            ConstraintViolation<T> error = constraintViolations.iterator().next();
            throw new ORIException(error.getPropertyPath() + " " + error.getMessage(), 400);
        }
    }   
}
