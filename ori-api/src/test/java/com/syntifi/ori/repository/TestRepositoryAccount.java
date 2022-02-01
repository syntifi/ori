package com.syntifi.ori.repository;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import com.syntifi.ori.exception.ORIException;
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
        Assertions.assertThrows(NoResultException.class, () -> accountRepository.findByTokenSymbolAndHash("ABC", "testAccount"));
    }

    @Test
    public void testAccountCheck() {
        var account = new Account();
        account.setHash("hash");
        account.setLabel("label");
        account.setPublicKey("key");
        var e = Assertions.assertThrowsExactly(ORIException.class, () -> accountRepository.check(account));
        Assertions.assertEquals("token must not be null", e.getMessage());
        Assertions.assertEquals(400, e.getStatus().getStatusCode());
    }
}
