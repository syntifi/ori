package com.syntifi.ori.service;

import java.time.OffsetDateTime;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.syntifi.ori.model.*;
import com.syntifi.ori.repository.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

/**
 * {@link TransferService} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TransferServiceTest {
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

        @Inject
        TransferService transferService;

        @Test
        @Order(1)
        public void testEmptyDB() {
                var now = OffsetDateTime.now();

                Assertions.assertEquals(0,
                                transferService.forwardGraphWalk("Chain", "from", now.minusHours(1), now).size());
                Assertions.assertEquals(0,
                                transferService.reverseGraphWalk("Chain", "to", now.minusHours(1), now).size());
        }

        @Test
        @Transactional
        @Order(2)
        public void testNonEmptyDB() {
                Chain chain = Chain.builder().name("Chain").build();
                Token token = Token.builder().symbol("ABC").chain(chain).name("ABC").unit(1E-18).build();
                chainRepository.persistAndFlush(chain);
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

                Assertions.assertEquals(2,
                                transferService.forwardGraphWalk("Chain", "from", now.minusHours(1), now).size());
                Assertions.assertEquals(1,
                                transferService.reverseGraphWalk("Chain", "to", now.minusHours(1), now).size());

                Assertions.assertEquals(0,
                                transferService.forwardGraphWalk("Chain", "from", now.minusHours(5), now.minusHours(2))
                                                .size());
                Assertions.assertEquals(0,
                                transferService.reverseGraphWalk("Chain", "to", now.minusHours(5), now.minusHours(2))
                                                .size());
        }

        @Transactional
        @Order(2)
        @Test
        public void testCleanDB() {
                var now = OffsetDateTime.now();

                transferRepository.deleteAll();
                accountRepository.deleteAll();
                blockRepository.deleteAll();
                tokenRepository.deleteAll();
                chainRepository.deleteAll();

                Assertions.assertEquals(0,
                                transferService.forwardGraphWalk("Chain", "from", now.minusHours(1), now).size());
                Assertions.assertEquals(0,
                                transferService.reverseGraphWalk("Chain", "to", now.minusHours(1), now).size());
        }

}
