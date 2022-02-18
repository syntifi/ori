package com.syntifi.ori.service;

import java.time.OffsetDateTime;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.AccountRepository;
import com.syntifi.ori.repository.BlockRepository;
import com.syntifi.ori.repository.TokenRepository;
import com.syntifi.ori.repository.TransactionRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

/**
 * {@link TransactionService} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TransactionServiceTest {
        @Inject
        TransactionRepository transactionRepository;

        @Inject
        TokenRepository tokenRepository;

        @Inject
        AccountRepository accountRepository;

        @Inject
        BlockRepository blockRepository;

        @Inject
        TransactionService transactionService;

        @Test
        @Order(1)
        public void testEmptyDB() {
                var now = OffsetDateTime.now();

                Assertions.assertEquals(0,
                                transactionService.forwardGraphWalk("ABC", "from", now.minusHours(1), now).size());
                Assertions.assertEquals(0,
                                transactionService.reverseGraphWalk("ABC", "to", now.minusHours(1), now).size());
        }

        @Test
        @Transactional
        @Order(2)
        public void testNonEmptyDB() {
                Token token = Token.builder().symbol("ABC").protocol("ABC").name("ABC").build();
                tokenRepository.persistAndFlush(token);

                Block block = Block.builder()
                                .hash("block")
                                .height(0L)
                                .era(0L)
                                .parent(null)
                                .root("root")
                                .validator("validator")
                                .timeStamp(OffsetDateTime.now())
                                .token(token)
                                .build();
                blockRepository.persistAndFlush(block);

                Account from = Account.builder().hash("from").label("label").publicKey("key").token(token).build();
                Account to = Account.builder().hash("to").label("label").publicKey("key").token(token).build();
                accountRepository.persistAndFlush(from);
                accountRepository.persistAndFlush(to);

                Transaction tx1 = Transaction.builder()
                                .amount(10.)
                                .block(block)
                                .fromAccount(from)
                                .toAccount(to)
                                .token(token)
                                .timeStamp(OffsetDateTime.now().minusSeconds(5))
                                .hash("tx1")
                                .build();
                Transaction tx2 = Transaction.builder()
                                .amount(5.)
                                .block(block)
                                .fromAccount(to)
                                .toAccount(from)
                                .token(token)
                                .timeStamp(OffsetDateTime.now())
                                .hash("tx2")
                                .build();
                transactionRepository.persistAndFlush(tx1);
                transactionRepository.persistAndFlush(tx2);

                var now = OffsetDateTime.now();

                Assertions.assertEquals(2,
                                transactionService.forwardGraphWalk("ABC", "from", now.minusHours(1), now).size());
                Assertions.assertEquals(1,
                                transactionService.reverseGraphWalk("ABC", "to", now.minusHours(1), now).size());

                Assertions.assertEquals(0,
                                transactionService.forwardGraphWalk("ABC", "from", now.minusHours(5), now.minusHours(2))
                                                .size());
                Assertions.assertEquals(0,
                                transactionService.reverseGraphWalk("ABC", "to", now.minusHours(5), now.minusHours(2))
                                                .size());
        }

        @Transactional
        @Order(2)
        @Test
        public void testCleanDB() {
                var now = OffsetDateTime.now();

                transactionRepository.deleteAll();
                accountRepository.deleteAll();
                blockRepository.deleteAll();
                tokenRepository.deleteAll();

                Assertions.assertEquals(0,
                                transactionService.forwardGraphWalk("ABC", "from", now.minusHours(1), now).size());
                Assertions.assertEquals(0,
                                transactionService.reverseGraphWalk("ABC", "to", now.minusHours(1), now).size());
        }

}
