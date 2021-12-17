package com.syntifi.ori;

import javax.inject.Inject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.TokenRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestRepositoryToken {
    @Inject
    TokenRepository tokenRepository;
    
    @Test
    public void testGetNonExistingToken() {
        var e = Assertions.assertThrowsExactly(ORIException.class, () -> tokenRepository.findBySymbol("testToken"));
        Assertions.assertEquals("Token symbol testToken not found!", e.getMessage());
        Assertions.assertEquals(404, e.getStatus().getStatusCode());
    }

    @Test
    public void testTokenCheck() {
        var token = new Token();
        token.setSymbol("SYM");
        token.setName("name");
        var e = Assertions.assertThrowsExactly(ORIException.class, () -> tokenRepository.check(token));
        Assertions.assertEquals("protocol must not be null", e.getMessage());
        Assertions.assertEquals(400, e.getStatus().getStatusCode());
    }
}
