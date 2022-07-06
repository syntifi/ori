package com.syntifi.ori.repository;

import com.syntifi.ori.model.Chain;
import com.syntifi.ori.model.Token;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TokenRepository} tests
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TokenRespositoryTest {
    @Inject
    TokenRepository tokenRepository;

    @Inject
    ChainRepository chainRepository;

    @Test
    public void testGetNonExistingToken() {
        Assertions.assertThrows(NoResultException.class,
                () -> tokenRepository.findByChainAndSymbol("CHAIN", "testToken"));
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
        Assertions.assertTrue(violatedFields.contains("chain"));
    }

    @Test
    @Order(2)
    public void testEmptyDB() {
        Assertions.assertFalse(tokenRepository.existsAlready("CHAIN", "ABC"));
        Assertions.assertEquals(0, tokenRepository.countByChainAndSymbol("CHAIN","ABC"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> tokenRepository.findByChainAndSymbol("CHAIN", "ABC"));
    }

    @Test
    @Transactional
    @Order(3)
    public void testNonEmptyDB() {
        Chain chain = Chain.builder().name("CHAIN").build();
        Token token = Token.builder().symbol("ABC").chain(chain).name("ABC").build();
        chainRepository.persistAndFlush(chain);
        tokenRepository.persistAndFlush(token);

        Assertions.assertTrue(tokenRepository.existsAlready("CHAIN", "ABC"));
        Assertions.assertEquals(1, tokenRepository.countByChainAndSymbol("CHAIN", "ABC"));
        Assertions.assertNotNull(tokenRepository.findByChainAndSymbol("CHAIN", "ABC"));
        Assertions.assertEquals(token, tokenRepository.findByChainAndSymbol("CHAIN", "ABC"));
    }

    @Test
    @Transactional
    @Order(4)
    public void testCleanDB() {
        tokenRepository.deleteAll();

        Assertions.assertFalse(tokenRepository.existsAlready("CHAIN", "ABC"));
        Assertions.assertEquals(0, tokenRepository.countByChainAndSymbol("CHAIN", "ABC"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> tokenRepository.findByChainAndSymbol("CHAIN", "ABC"));
    }
}
