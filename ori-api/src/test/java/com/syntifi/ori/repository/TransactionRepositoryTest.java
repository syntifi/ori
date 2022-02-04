package com.syntifi.ori.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.model.Transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TransactionRepositoryTest {
    @Inject
    TransactionRepository transactionRepository;

    @Inject
    TokenRepository tokenRepository;

    @Inject
    AccountRepository accountRepository;

    @Inject
    BlockRepository blockRepository;

    @Test
    @Order(1)
    public void testGetNonExistingTransaction() {
        Assertions.assertThrows(NoResultException.class,
                () -> transactionRepository.findByTokenSymbolAndHash("ABC", "testTransaction"));
    }

    @Test
    @Order(2)
    public void testTransactionCheck() {
        var transaction = new Transaction();
        transaction.setAmount(10.0);
        transaction.setBlock(new Block());
        transaction.setFromAccount(new Account());
        transaction.setToAccount(new Account());
        transaction.setTimeStamp(OffsetDateTime.now());
        var e = Assertions.assertThrowsExactly(ConstraintViolationException.class,
                () -> transactionRepository.check(transaction));
        Assertions.assertEquals(2, e.getConstraintViolations().size());
        List<String> violatedFields = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath().iterator().next().getName()).collect(Collectors.toList());
        Assertions.assertTrue(violatedFields.contains("token"));
        Assertions.assertTrue(violatedFields.contains("hash"));
    }

    @Test
    @Order(3)
    public void testEmptyDB() {
        var now = OffsetDateTime.now();

        Assertions.assertFalse(transactionRepository.existsAlready("ABC", "tx1"));
        Assertions.assertEquals(0, transactionRepository.countByTokenSymbolAndHash("ABC", "tx2"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> transactionRepository.findByTokenSymbolAndHash("ABC", "tx1"));
        Assertions.assertEquals(0, transactionRepository.getOutgoingTransactions("ABC", "from").size());
        Assertions.assertEquals(0, transactionRepository.getOutgoingTransactions("ABC", "from",
                now.minusHours(1), now).size());
        Assertions.assertEquals(0, transactionRepository.getIncomingTransactions("ABC", "to").size());
        Assertions.assertEquals(0, transactionRepository.getIncomingTransactions("ABC", "to",
                now.minusHours(1), now).size());
        Assertions.assertEquals(0, transactionRepository
                .getTransactionsByTokenSymbolAndFromAccountAndToAccount("ABC", "from", "to").size());
        Assertions.assertEquals(0,
                transactionRepository.getTransactionsByTokenSymbolAndFromAccountAndToAccountBetweenTimeStamps(
                        "ABC", "from", "to", now.minusHours(1), now).size());
        Assertions.assertEquals(0, transactionRepository.getTransactionsByTokenSymbolAndAccount("ABC", "from").size());
        Assertions.assertEquals(0, transactionRepository.getAllTransactions("ABC").size());
        Assertions.assertFalse(transactionRepository.getAllTransactionsByTokenSymbolFromDateToDate("ABC",
                now.minusHours(1), now, Sort.descending("time_stamp"), 100).hasNextPage());
    }

    @Test
    @Transactional
    @Order(4)
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

        Assertions.assertTrue(transactionRepository.existsAlready("ABC", "tx1"));
        Assertions.assertEquals(1, transactionRepository.countByTokenSymbolAndHash("ABC", "tx2"));
        Assertions.assertNotNull(transactionRepository.findByTokenSymbolAndHash("ABC", "tx1"));
        Assertions.assertEquals(1, transactionRepository.getOutgoingTransactions("ABC", "from").size());
        Assertions.assertEquals(1, transactionRepository.getOutgoingTransactions("ABC", "from",
                now.minusHours(1), now).size());
        Assertions.assertEquals(0, transactionRepository.getOutgoingTransactions("ABC", "from",
                now.minusHours(2), now.minusHours(1)).size());
        Assertions.assertEquals(1, transactionRepository.getIncomingTransactions("ABC", "to").size());
        Assertions.assertEquals(1, transactionRepository.getIncomingTransactions("ABC", "to",
                now.minusHours(1), now).size());
        Assertions.assertEquals(0, transactionRepository.getIncomingTransactions("ABC", "to",
                now.minusHours(2), now.minusHours(1)).size());
        Assertions.assertEquals(1, transactionRepository
                .getTransactionsByTokenSymbolAndFromAccountAndToAccount("ABC", "from", "to").size());
        Assertions.assertEquals(1,
                transactionRepository
                        .getTransactionsByTokenSymbolAndFromAccountAndToAccountBetweenTimeStamps("ABC", "from", "to",
                                now.minusHours(1), now)
                        .size());
        Assertions.assertEquals(2, transactionRepository.getTransactionsByTokenSymbolAndAccount("ABC", "from").size());
        Assertions.assertEquals(2, transactionRepository.getTransactionsByTokenSymbolAndAccount("ABC", "to").size());
        Assertions.assertEquals(2, transactionRepository.getAllTransactions("ABC").size());
        Assertions.assertEquals(2, transactionRepository.getAllTransactionsByTokenSymbolFromDateToDate("ABC",
                now.minusHours(1), now, Sort.descending("time_stamp"), 100).firstPage().count());
    }

    @Transactional
    @Order(5)
    @Test
    public void testCleanDB() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        blockRepository.deleteAll();
        tokenRepository.deleteAll();

        var now = OffsetDateTime.now();
        Assertions.assertFalse(transactionRepository.existsAlready("ABC", "tx1"));
        Assertions.assertEquals(0, transactionRepository.countByTokenSymbolAndHash("ABC", "tx2"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> transactionRepository.findByTokenSymbolAndHash("ABC", "tx1"));
        Assertions.assertEquals(0, transactionRepository.getOutgoingTransactions("ABC", "from").size());
        Assertions.assertEquals(0, transactionRepository.getOutgoingTransactions("ABC", "from",
                now.minusHours(1), now).size());
        Assertions.assertEquals(0, transactionRepository.getIncomingTransactions("ABC", "to").size());
        Assertions.assertEquals(0, transactionRepository.getIncomingTransactions("ABC", "to",
                now.minusHours(1), now).size());
        Assertions.assertEquals(0, transactionRepository
                .getTransactionsByTokenSymbolAndFromAccountAndToAccount("ABC", "from", "to").size());
        Assertions.assertEquals(0,
                transactionRepository.getTransactionsByTokenSymbolAndFromAccountAndToAccountBetweenTimeStamps(
                        "ABC", "from", "to", now.minusHours(1), now).size());
        Assertions.assertEquals(0, transactionRepository.getTransactionsByTokenSymbolAndAccount("ABC", "from").size());
        Assertions.assertEquals(0, transactionRepository.getAllTransactions("ABC").size());
        //Assertions.assertEquals(0, transactionRepository.getAllTransactionsByTokenSymbolFromDateToDate("ABC",
        //        now.minusHours(1), now, Sort.empty(), 0, 100).size());
    }
}