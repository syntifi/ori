package com.syntifi.ori.repository;

import java.time.OffsetDateTime;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;

/**
 * {@link BlockRepository} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class BlockRespositoryTest {
    @Inject
    BlockRepository blockRepository;

    @Inject
    TokenRepository tokenRepository;

    @Test
    @Order(1)
    public void testGetNonExistingBlock() {
        Assertions.assertThrows(NoResultException.class,
                () -> blockRepository.findByTokenSymbolAndHash("symbol", "testBlock"));
    }

    @Test
    @Order(2)
    public void testEmptyDB() {
        Assertions.assertFalse(blockRepository.existsAlready("ABC", "Block"));
        Assertions.assertEquals(0, blockRepository.countByTokenSymbolAndHash("ABC", "Block"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> blockRepository.findByTokenSymbolAndHash("ABC", "Block"));
        Assertions.assertNull(blockRepository.getLastBlock("ABC"));
        Assertions.assertEquals(0, blockRepository.getBlocks("ABC").size());
        Assertions.assertFalse(blockRepository.existsAnyByToken("ABC"));
    }

    @Test
    @Transactional
    @Order(3)
    public void testNonEmptyDB() {
        Token token = Token.builder().symbol("ABC").protocol("ABC").name("ABC").build();

        Block block = Block.builder()
                .hash("Block")
                .height(0L)
                .era(0L)
                .parent(null)
                .root("root")
                .validator("validator")
                .timeStamp(OffsetDateTime.now())
                .token(token)
                .build();
        blockRepository.persistAndFlush(block);

        Assertions.assertTrue(blockRepository.existsAlready("ABC", "Block"));
        Assertions.assertEquals(1, blockRepository.countByTokenSymbolAndHash("ABC", "Block"));
        Assertions.assertNotNull(blockRepository.findByTokenSymbolAndHash("ABC", "Block"));
        Assertions.assertNotNull(blockRepository.getLastBlock("ABC"));
        Assertions.assertEquals(1, blockRepository.getBlocks("ABC").size());
        Assertions.assertTrue(blockRepository.existsAnyByToken("ABC"));
    }

    @Test
    @Transactional
    @Order(4)
    public void testCleanDB() {
        blockRepository.deleteAll();
        tokenRepository.deleteAll();

        Assertions.assertFalse(blockRepository.existsAlready("ABC", "Block"));
        Assertions.assertEquals(0, blockRepository.countByTokenSymbolAndHash("ABC", "Block"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> blockRepository.findByTokenSymbolAndHash("ABC", "Block"));
        Assertions.assertNull(blockRepository.getLastBlock("ABC"));
        Assertions.assertEquals(0, blockRepository.getBlocks("ABC").size());
        Assertions.assertFalse(blockRepository.existsAnyByToken("ABC"));
    }

}