package com.syntifi.ori.chains.cspr.reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import com.syntifi.ori.chains.cspr.AbstractCsprChainTest;
import com.syntifi.ori.chains.cspr.CsprChainCrawlerJob;
import com.syntifi.ori.chains.cspr.CsprTestChainConfig;
import com.syntifi.ori.chains.cspr.model.CsprChainData;

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
@ContextConfiguration(classes = { CsprTestChainConfig.class, CsprChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class CsprChainReaderTest extends AbstractCsprChainTest {

    @Autowired
    private CsprChainReader reader;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    public void testReader_with_resources_test_data() throws IOException, InterruptedException {
        CsprChainData chainData;
        while ((chainData = reader.read()) != null) {
            assertNotNull(chainData);
        }
        assertNull(chainData);
    }
}
