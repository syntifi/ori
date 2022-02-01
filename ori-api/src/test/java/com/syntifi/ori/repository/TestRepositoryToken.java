package com.syntifi.ori.repository;

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
        Assertions.assertEquals("protocol",
                e.getConstraintViolations().iterator().next().getPropertyPath().iterator().next().getName());
    }
}
