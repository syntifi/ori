package com.syntifi.ori.chains.base.reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import com.syntifi.ori.chains.base.MockChainConfig;
import com.syntifi.ori.chains.base.MockChainCrawlerJob;
import com.syntifi.ori.chains.base.model.MockChainData;
import com.syntifi.ori.chains.base.service.MockTestChainService;

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
public class MockChainReaderTest {

    @Autowired
    private MockTestChainService mockTestChainService;

    @Autowired
    private MockChainReader reader;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @BeforeEach
    void beforeEach(@Autowired MockTestChainService service) {
        service.reset();
    }

    @Test
    public void testReader() throws IOException, InterruptedException {
        MockChainData chainData;
        while ((chainData = reader.read()) != null) {
            assertNotNull(mockTestChainService.getBlock(chainData.getChainBlock().getHash()));
        }

        assertNull(chainData);
    }
}
