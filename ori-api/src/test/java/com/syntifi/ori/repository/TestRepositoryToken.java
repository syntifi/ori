package com.syntifi.ori.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;

import com.syntifi.ori.model.Token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestRepositoryToken {
    @Inject
    TokenRepository tokenRepository;

    @Test
    public void testGetNonExistingToken() {
        Assertions.assertThrows(NoResultException.class, () -> tokenRepository.findBySymbol("testToken"));
    }

    @Test
    public void testTokenCheck() {
        var token = new Token();
        token.setSymbol("SYM");
        token.setName("name");
        var e = Assertions.assertThrowsExactly(ConstraintViolationException.class, () -> tokenRepository.check(token));
        Assertions.assertEquals(1, e.getConstraintViolations().size());
        List<String> violatedFields = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath().iterator().next().getName()).collect(Collectors.toList());
        Assertions.assertTrue(violatedFields.contains("protocol"));
    }
}
