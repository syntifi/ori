package com.syntifi.ori.chains.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.syntifi.ori.chains.base.service.MockTestChainService;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.dto.BlockDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBatchTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { MockChainConfig.class, MockChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class MockChainCrawlerJobTest {

    @Autowired
    public OriClient oriClient;

    @Autowired
    public MockTestChainService service;

    @Autowired
    public OriChainConfigProperties oriChainConfigProperties;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeEach
    void beforeEach(@Autowired MockTestChainService service) {
        service.reset();
    }

    @Test
    void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());

        // TODO: Improve comparison input/output
        BlockDTO oriBlock = oriClient.getLastBlock(oriChainConfigProperties.getChainTokenSymbol());
        assertNotNull(service.getBlock(oriBlock.getHash()));
    }

    @Test
    void testJob_withChunkSize1() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());

        // TODO: Improve comparison input/output
        BlockDTO oriBlock = oriClient.getLastBlock(oriChainConfigProperties.getChainTokenSymbol());
        assertNotNull(service.getBlock(oriBlock.getHash()));
    }
}
