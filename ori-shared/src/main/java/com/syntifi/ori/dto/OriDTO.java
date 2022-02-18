package com.syntifi.ori.dto;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * Ori DTO for every object that needs validation or other common funcionality
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public interface OriDTO {
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public default void check() throws ConstraintViolationException {
        Set<ConstraintViolation<OriDTO>> constraintViolations = validator.validate(this);
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
