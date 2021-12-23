package com.syntifi.ori.repository;

import java.util.Date;

import javax.inject.Inject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestRepositoryTransaction {
    @Inject
    TransactionRepository transactionRepository;

    @Test
    public void testGetNonExistingTransaction() {
        Assertions.assertEquals(null, transactionRepository.findByHash("testTransaction"));
    }

    @Test
    public void testBlockCheck() {
        var transaction = new Transaction();
        transaction.setAmount(10.0);
        transaction.setBlock(new Block());
        transaction.setFromAccount(new Account());
        transaction.setToAccount(new Account());
        transaction.setTimeStamp(new Date());
        var e = Assertions.assertThrowsExactly(ORIException.class, () -> transactionRepository.check(transaction));
        Assertions.assertEquals("hash must not be null", e.getMessage());
        Assertions.assertEquals(400, e.getStatus().getStatusCode());
    }
}