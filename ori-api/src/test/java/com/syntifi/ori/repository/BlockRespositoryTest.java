package com.syntifi.ori.repository;

import java.time.OffsetDateTime;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Chain;

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

    @Inject
    ChainRepository chainRepository;

    @Test
    @Order(1)
    public void testGetNonExistingBlock() {
        Assertions.assertThrows(NoResultException.class,
                () -> blockRepository.findByChainNameAndHash("symbol", "testBlock"));
    }

    @Test
    @Order(2)
    public void testEmptyDB() {
        Assertions.assertFalse(blockRepository.existsAlready("ABC", "Block"));
        Assertions.assertEquals(0, blockRepository.countByChainNameAndHash("ABC", "Block"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> blockRepository.findByChainNameAndHash("ABC", "Block"));
        Assertions.assertNull(blockRepository.getLastBlock("ABC"));
        Assertions.assertEquals(0, blockRepository.getAllBlocks("ABC").page(0,25).list().size());
        Assertions.assertFalse(blockRepository.existsAnyByChain("ABC"));
    }

    @Test
    @Transactional
    @Order(3)
    public void testNonEmptyDB() {
        Chain chain = Chain.builder().name("CHAIN").build();

        Block block = Block.builder()
                .hash("Block")
                .height(0L)
                .era(0L)
                .parent(null)
                .root("root")
                .validator("validator")
                .timeStamp(OffsetDateTime.now())
                .chain(chain)
                .build();
        chainRepository.persistAndFlush(chain);
        blockRepository.persistAndFlush(block);

        Assertions.assertTrue(blockRepository.existsAlready("CHAIN", "Block"));
        Assertions.assertEquals(1, blockRepository.countByChainNameAndHash("CHAIN", "Block"));
        Assertions.assertNotNull(blockRepository.findByChainNameAndHash("CHAIN", "Block"));
        Assertions.assertNotNull(blockRepository.getLastBlock("CHAIN"));
        Assertions.assertEquals(1, blockRepository.getAllBlocks("CHAIN").page(0,25).list().size());
        Assertions.assertTrue(blockRepository.existsAnyByChain("CHAIN"));
    }

    @Test
    @Transactional
    @Order(4)
    public void testCleanDB() {
        blockRepository.deleteAll();
        tokenRepository.deleteAll();

        Assertions.assertFalse(blockRepository.existsAlready("ABC", "Block"));
        Assertions.assertEquals(0, blockRepository.countByChainNameAndHash("ABC", "Block"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> blockRepository.findByChainNameAndHash("ABC", "Block"));
        Assertions.assertNull(blockRepository.getLastBlock("ABC"));
        Assertions.assertEquals(0, blockRepository.getAllBlocks("ABC").page(0,25).list().size());
        Assertions.assertFalse(blockRepository.existsAnyByChain("ABC"));
    }

}