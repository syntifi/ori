package com.syntifi.ori.repository;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

public interface OriRepository<T> extends PanacheRepository<T> {
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public default void check(T obj) throws ConstraintViolationException {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(
                    String.format("Constraint violation for object of type %s", obj.getClass().getTypeName()),
                    constraintViolations);
        }
    }
}
