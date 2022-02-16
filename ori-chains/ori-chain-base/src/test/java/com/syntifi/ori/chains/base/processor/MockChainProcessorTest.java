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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    protected static final Log logger = LogFactory.getLog(MockChainProcessorTest.class);

    @Autowired
    private MockTestChainService service;

    @Autowired
    private MockChainProcessor processor;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @BeforeEach
    void beforeEach() {
        service.reset();
    }

    @Test
    public void testProcessor() throws Exception {
        MockChainBlock chainBlock = service.getNextBlock();

        List<MockChainTransfer> chainTransfers = new LinkedList<>();

        chainTransfers.addAll(service.getTransfers(chainBlock.getHash()));

        MockChainData chainData = MockChainData.builder()
                .chainBlock(chainBlock)
                .chainTransfers(chainTransfers)
                .build();

        OriData oriData = processor.process(chainData);

        assertNotNull(oriData);
    }
}
