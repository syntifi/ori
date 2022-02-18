package com.syntifi.ori.chains.cspr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.client.OriClient;

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
@ContextConfiguration(classes = { CsprTestChainConfig.class, CsprChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class CsprChainCrawlerJobTest extends AbstractCsprChainTest {

    @Autowired
    public OriClient oriClient;

    @Autowired
    public CasperService service;

    @Autowired
    public OriChainConfigProperties oriChainConfigProperties;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
    }
}
