package com.syntifi.ori.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;

import com.syntifi.ori.model.Account;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestRepositoryAccount {
    @Inject
    AccountRepository accountRepository;

    @Test
    public void testGetNonExistingAccount() {
        Assertions.assertThrows(NoResultException.class,
                () -> accountRepository.findByTokenSymbolAndHash("ABC", "testAccount"));
    }

    @Test
    public void testAccountCheck() {
        var account = new Account();
        account.setHash("hash");
        account.setLabel("label");
        account.setPublicKey("key");
        var e = Assertions.assertThrowsExactly(ConstraintViolationException.class,
                () -> accountRepository.check(account));
        Assertions.assertEquals(1, e.getConstraintViolations().size());
        List<String> violatedFields = e.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath().iterator().next().getName()).collect(Collectors.toList());
        Assertions.assertTrue(violatedFields.contains("token"));        
    }
}
