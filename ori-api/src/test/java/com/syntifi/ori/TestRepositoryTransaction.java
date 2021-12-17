package com.syntifi.ori;

import java.util.Date;

import javax.inject.Inject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.TransactionRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestRepositoryTransaction {
    @Inject
    TransactionRepository transactionRepository;

    @Test
    public void testGetNonExistingTransaction() {
        var e = Assertions.assertThrowsExactly(ORIException.class,
                () -> transactionRepository.findByHash("testTransaction"));
        Assertions.assertEquals("Transaction hash testTransaction not found!", e.getMessage());
        Assertions.assertEquals(404, e.getStatus().getStatusCode());
    }

    @Test
    public void testBlockCheck() {
        var transaction = new Transaction();
        transaction.setAmount(10.0);
        transaction.setBlock(new Block());
        transaction.setFrom(new Account());
        transaction.setTo(new Account());
        transaction.setTimeStamp(new Date());
        var e = Assertions.assertThrowsExactly(ORIException.class, () -> transactionRepository.check(transaction));
        Assertions.assertEquals("hash must not be null", e.getMessage());
        Assertions.assertEquals(400, e.getStatus().getStatusCode());
    }
}