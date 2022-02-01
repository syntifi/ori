package com.syntifi.ori.repository;

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
        Assertions.assertEquals("token",
                e.getConstraintViolations().iterator().next().getPropertyPath().iterator().next().getName());
    }
}
