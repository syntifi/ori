package com.syntifi.ori;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

public class TestModelAccount {
    
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    public void testNullToken() {
        var account = new Account();
        account.setToken(null);
        account.setHash("hash");
        account.setPublicKey("key");
        account.setLabel("from");
        Set<ConstraintViolation<Account>> constraintViolations = validator.validate(account);
        Assertions.assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testNotNullToken() {
        var account = new Account();
        var token = new Token();
        token.setSymbol("symbol");
        token.setName("name");
        token.setProtocol("protocol");
        account.setToken(token);
        account.setHash("hash");
        account.setPublicKey("key");
        account.setLabel("from");
        Set<ConstraintViolation<Account>> constraintViolations = validator.validate(account);
        Assertions.assertEquals(0, constraintViolations.size());
    }
    @Test
    public void testSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        var token = new JsonObject();
        token.put("symbol", "SYM");
        token.put("name", "name");
        token.put("protocol", "protocol");
        var account = new JsonObject();
        account.put("token", token);
        account.put("hash", "from");
        account.put("publicKey", "key");
        account.put("label", "from");
        Assertions.assertDoesNotThrow(() -> mapper.readValue(account.toString(), Account.class));
    }
}
