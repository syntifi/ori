package com.syntifi.ori.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Chain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

/**
 * {@link AccountRepository} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class AccountRespositoryTest {
    @Inject
    AccountRepository accountRepository;

    @Inject
    ChainRepository chainRepository;

    @Test
    @Order(1)
    public void testGetNonExistingAccount() {
        Assertions.assertThrows(NoResultException.class,
                () -> accountRepository.findByChainNameAndHash("Chain", "testAccount"));
    }

    @Test
    @Order(2)
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
        Assertions.assertTrue(violatedFields.contains("chain"));
    }

    @Test
    @Order(3)
    public void testEmptyDB() {
        Assertions.assertFalse(accountRepository.existsAlready("Chain", "Account"));
        Assertions.assertEquals(0, accountRepository.countByChainNameAndHash("Chain", "Account"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> accountRepository.findByChainNameAndHash("Chain", "Account"));
    }

    @Test
    @Transactional
    @Order(4)
    public void testNonEmptyDB() {
        Chain chain = Chain.builder().name("Chain").build();
        chainRepository.persistAndFlush(chain);

        Account account = Account.builder().hash("Account").label("Label").publicKey("Key").chain(chain).build();
        accountRepository.persistAndFlush(account);

        Assertions.assertTrue(accountRepository.existsAlready("Chain", "Account"));
        Assertions.assertEquals(1, accountRepository.countByChainNameAndHash("Chain", "Account"));
        Assertions.assertNotNull(accountRepository.findByChainNameAndHash("Chain", "Account"));
    }

    @Test
    @Transactional
    @Order(5)
    public void testCleanDB() {
        accountRepository.deleteAll();
        chainRepository.deleteAll();

        Assertions.assertFalse(accountRepository.existsAlready("Chain", "Account"));
        Assertions.assertEquals(0, accountRepository.countByChainNameAndHash("Chain", "Account"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> accountRepository.findByChainNameAndHash("Chain", "Account"));
    }
}
