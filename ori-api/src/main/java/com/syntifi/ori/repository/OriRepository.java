package com.syntifi.ori.repository;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

/**
 * Base Ori Repository for helper and common methods
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public interface OriRepository<T> extends PanacheRepository<T> {
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Checks model for any constraint violation
     * 
     * @param model the target model to validate
     * @throws ConstraintViolationException thrown when any violation is found
     */
    public default void check(T model) throws ConstraintViolationException {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(model);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(
                    String.format("Constraint violation for object of type %s (fields: %s)",
                            this.getClass().getSimpleName(),
                            constraintViolations.stream()
                                    .map(v -> v.getPropertyPath().toString())
                                    .collect(Collectors.joining(","))),
                    constraintViolations);
        }
    }
}
