package com.syntifi.ori.repository;

import com.syntifi.ori.model.Chain;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChainRepositoryTest {
    @Inject
    ChainRepository chainRepository;

    @Test
    public void testGetNonExistingChain() {
        Assertions.assertThrows(NoResultException.class, () -> chainRepository.findByName("testChain"));
    }

    @Test
    @Order(1)
    public void testChainCheck() {
        var chain = new Chain();
        var e = Assertions.assertThrowsExactly(ConstraintViolationException.class, () -> chainRepository.check(chain));
        Assertions.assertEquals(1, e.getConstraintViolations().size());
        List<String> violatedFields = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath().iterator().next().getName()).collect(Collectors.toList());
        Assertions.assertTrue(violatedFields.contains("name"));
    }

    @Test
    @Order(2)
    public void testEmptyDB() {
        Assertions.assertFalse(chainRepository.existsAlready("Chain"));
        Assertions.assertEquals(0, chainRepository.countByName("Chain"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> chainRepository.findByName("Chain"));
    }

    @Test
    @Transactional
    @Order(3)
    public void testNonEmptyDB() {
        Chain chain = Chain.builder().name("Chain").build();
        chainRepository.persistAndFlush(chain);

        Assertions.assertTrue(chainRepository.existsAlready("Chain"));
        Assertions.assertEquals(1, chainRepository.countByName("Chain"));
        Assertions.assertNotNull(chainRepository.findByName("Chain"));
        Assertions.assertEquals(chain, chainRepository.findByName("Chain"));
    }

    @Test
    @Transactional
    @Order(4)
    public void testCleanDB() {
        chainRepository.deleteAll();

        Assertions.assertFalse(chainRepository.existsAlready("Chain"));
        Assertions.assertEquals(0, chainRepository.countByName("Chain"));
        Assertions.assertThrowsExactly(NoResultException.class,
                () -> chainRepository.findByName("Chain"));
    }
}
