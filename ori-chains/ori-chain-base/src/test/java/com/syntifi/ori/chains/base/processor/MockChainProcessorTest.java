package com.syntifi.ori.chains.base.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.chains.base.MockChainConfig;
import com.syntifi.ori.chains.base.MockChainCrawlerJob;
import com.syntifi.ori.chains.base.model.MockChainBlock;
import com.syntifi.ori.chains.base.model.MockChainData;
import com.syntifi.ori.chains.base.model.MockChainTransfer;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.chains.base.service.MockTestChainService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBatchTest
@ContextConfiguration(classes = { MockChainConfig.class, MockChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class MockChainProcessorTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private MockChainProcessor processor;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @BeforeEach
    void beforeEach(@Autowired MockTestChainService service) {
        service.reset();
    }

    @Test
    public void testProcessor(@Autowired MockTestChainService mockTestChainService) throws Exception {

        LOGGER.info("testProcessor - mockTestChainService is null? " + (mockTestChainService == null));
        MockChainBlock chainBlock = mockTestChainService.getBlock();
        LOGGER.info("testProcessor - chainBlock is null? " + (chainBlock == null));
        LOGGER.info("testProcessor - chainBlock hash: " + chainBlock.getHash());

        List<MockChainTransfer> chainTransfers = new LinkedList<>();

        chainTransfers.addAll(mockTestChainService.getTransfers(chainBlock.getHash()));

        MockChainData chainData = MockChainData.builder()
                .chainBlock(chainBlock)
                .chainTransfers(chainTransfers)
                .build();

        OriData oriData = processor.process(chainData);

        assertNotNull(oriData);
    }
}
