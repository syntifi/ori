package com.syntifi.ori.chains.eth.reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import com.syntifi.ori.chains.eth.AbstractEthChainTest;
import com.syntifi.ori.chains.eth.EthChainCrawlerJob;
import com.syntifi.ori.chains.eth.EthTestChainConfig;
import com.syntifi.ori.chains.eth.model.EthChainData;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBatchTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { EthTestChainConfig.class, EthChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class EthChainReaderTest extends AbstractEthChainTest {

    @Autowired
    private EthChainReader reader;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    public void testReader_with_resources_test_data() throws IOException, InterruptedException {
        EthChainData chainData;
        while ((chainData = reader.read()) != null) {
            assertNotNull(chainData);
        }
        assertNull(chainData);
    }
}
