package com.syntifi.ori.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import com.syntifi.ori.model.Token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

/**
 * {@link TokenRepository} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TokenRespositoryTest {
    @Inject
    TokenRepository tokenRepository;

    @Test
    public void testGetNonExistingToken() {
        Assertions.assertThrows(NoResultException.class, () -> tokenRepository.findBySymbol("testToken"));
    }

    @Test
    @Order(1)
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

    @Test
    @Order(2)
    public void testEmptyDB() {
        Assertions.assertFalse(tokenRepository.existsAlready("ABC"));
        Assertions.assertEquals(0, tokenRepository.countBySymbol("ABC"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> tokenRepository.findBySymbol("ABC"));
    }

    @Test
    @Transactional
    @Order(3)
    public void testNonEmptyDB() {
        Token token = Token.builder().symbol("ABC").protocol("ABC").name("ABC").build();
        tokenRepository.persistAndFlush(token);

        Assertions.assertTrue(tokenRepository.existsAlready("ABC"));
        Assertions.assertEquals(1, tokenRepository.countBySymbol("ABC"));
        Assertions.assertNotNull(tokenRepository.findBySymbol("ABC"));
        Assertions.assertEquals(token, tokenRepository.findBySymbol("ABC"));
    }

    @Test
    @Transactional
    @Order(4)
    public void testCleanDB() {
        tokenRepository.deleteAll();

        Assertions.assertFalse(tokenRepository.existsAlready("ABC"));
        Assertions.assertEquals(0, tokenRepository.countBySymbol("ABC"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> tokenRepository.findBySymbol("ABC"));
    }
}
