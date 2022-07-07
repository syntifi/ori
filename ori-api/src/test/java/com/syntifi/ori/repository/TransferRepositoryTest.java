package com.syntifi.ori.repository;

import com.syntifi.ori.model.*;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TransferRepository} tests
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TransferRepositoryTest {
    @Inject
    TransferRepository transferRepository;

    @Inject
    TokenRepository tokenRepository;

    @Inject
    AccountRepository accountRepository;

    @Inject
    BlockRepository blockRepository;

    @Inject
    ChainRepository chainRepository;

    @Test
    @Order(1)
    public void testGetNonExistingTransfer() {
        Assertions.assertThrows(NoResultException.class,
                () -> transferRepository.findByChainNameAndHash("Chain", "testTransfer"));
    }

    @Test
    @Order(2)
    public void testTransferCheck() {
        var transfer = new Transfer();
        transfer.setAmount(10.0);
        transfer.setBlock(new Block());
        transfer.setFromAccount(new Account());
        transfer.setToAccount(new Account());
        transfer.setTimeStamp(OffsetDateTime.now());
        var e = Assertions.assertThrowsExactly(ConstraintViolationException.class,
                () -> transferRepository.check(transfer));
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

        Assertions.assertFalse(transferRepository.existsAlready("Chain", "tx1"));
        Assertions.assertEquals(0,
                transferRepository.countByChainNameAndHash("ABC", "tx2"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> transferRepository
                        .findByChainNameAndHash("Chain", "tx1"));
        Assertions.assertEquals(0,
                transferRepository
                        .getOutgoingTransfers("Chain", "from")
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getOutgoingTransfers("Chain", "from", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getIncomingTransfers("Chain", "to")
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getIncomingTransfers("Chain", "to", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getTransfersFromAccountAndToAccount("Chain", "from", "to")
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getTransfersFromAccountAndToAccount(
                                "Chain", "from", "to", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getAllTransfersForAccount("Chain", "from")
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getAllTransfers("Chain")
                        .list().size());
        Assertions.assertFalse(
                transferRepository
                        .getAllTransfers("Chain", now.minusHours(1), now, Sort.descending("time_stamp"))
                        .page(Page.ofSize(25)).hasNextPage());
    }

    @Test
    @Transactional
    @Order(4)
    public void testNonEmptyDB() {
        Chain chain = Chain.builder().name("Chain").build();
        chainRepository.persistAndFlush(chain);

        Token token = Token.builder().symbol("ABC").chain(chain).name("ABC").unit(1E-18).build();
        tokenRepository.persistAndFlush(token);

        Block block = Block.builder()
                .hash("block")
                .height(0L)
                .era(0L)
                .parent(null)
                .root("root")
                .validator("validator")
                .timeStamp(OffsetDateTime.now())
                .chain(chain)
                .build();
        blockRepository.persistAndFlush(block);

        Account from = Account.builder().hash("from").label("label").publicKey("key").chain(chain).build();
        Account to = Account.builder().hash("to").label("label").publicKey("key").chain(chain).build();
        accountRepository.persistAndFlush(from);
        accountRepository.persistAndFlush(to);

        Transfer tx1 = Transfer.builder()
                .amount(10.)
                .block(block)
                .fromAccount(from)
                .toAccount(to)
                .token(token)
                .timeStamp(OffsetDateTime.now().minusSeconds(5))
                .hash("tx1")
                .build();
        Transfer tx2 = Transfer.builder()
                .amount(5.)
                .block(block)
                .fromAccount(to)
                .toAccount(from)
                .token(token)
                .timeStamp(OffsetDateTime.now())
                .hash("tx2")
                .build();
        transferRepository.persistAndFlush(tx1);
        transferRepository.persistAndFlush(tx2);

        var now = OffsetDateTime.now();

        Assertions.assertTrue(transferRepository
                .existsAlready("Chain", "tx1"));
        Assertions.assertEquals(1,
                transferRepository
                        .countByChainNameAndHash("Chain", "tx2"));
        Assertions.assertNotNull(transferRepository
                .findByChainNameAndHash("Chain", "tx1"));
        Assertions.assertEquals(1,
                transferRepository
                        .getOutgoingTransfers("Chain", "from").list().size());
        Assertions.assertEquals(1,
                transferRepository
                        .getOutgoingTransfers("Chain", "from", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getOutgoingTransfers("Chain", "from", now.minusHours(2), now.minusHours(1))
                        .list().size());
        Assertions.assertEquals(1,
                transferRepository.getIncomingTransfers("Chain", "to")
                        .list().size());
        Assertions.assertEquals(1,
                transferRepository
                        .getIncomingTransfers("Chain", "to", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getIncomingTransfers("Chain", "to", now.minusHours(2), now.minusHours(1))
                        .list().size());
        Assertions.assertEquals(1,
                transferRepository
                        .getTransfersFromAccountAndToAccount("Chain", "from", "to")
                        .list().size());
        Assertions.assertEquals(1,
                transferRepository
                        .getTransfersFromAccountAndToAccount("Chain", "from", "to", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(2,
                transferRepository
                        .getAllTransfersForAccount("Chain", "from")
                        .list().size());
        Assertions.assertEquals(2,
                transferRepository
                        .getAllTransfersForAccount("Chain", "to")
                        .list().size());
        Assertions.assertEquals(2,
                transferRepository
                        .getAllTransfers("Chain")
                        .list().size());
        Assertions.assertEquals(2,
                transferRepository
                        .getAllTransfers("Chain", now.minusHours(1), now, Sort.descending("time_stamp"))
                        .page(Page.ofSize(25)).firstPage().count());
    }

    @Transactional
    @Order(5)
    @Test
    public void testCleanDB() {
        transferRepository.deleteAll();
        accountRepository.deleteAll();
        blockRepository.deleteAll();
        tokenRepository.deleteAll();
        chainRepository.deleteAll();

        var now = OffsetDateTime.now();
        Assertions.assertFalse(transferRepository
                .existsAlready("Chain", "tx1"));
        Assertions.assertEquals(0,
                transferRepository
                        .countByChainNameAndHash("Chain", "tx2"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> transferRepository
                        .findByChainNameAndHash("Chain", "tx1"));
        Assertions.assertEquals(0,
                transferRepository
                        .getOutgoingTransfers("Chain", "from")
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository.getOutgoingTransfers("Chain", "from", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository.getIncomingTransfers("Chain", "to")
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository.getIncomingTransfers("Chain", "to", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getTransfersFromAccountAndToAccount("Chain", "from", "to")
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getTransfersFromAccountAndToAccount("Chain", "from", "to", now.minusHours(1), now)
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getAllTransfersForAccount("Chain", "from")
                        .list().size());
        Assertions.assertEquals(0,
                transferRepository
                        .getAllTransfers("Chain")
                        .list().size());
    }
}