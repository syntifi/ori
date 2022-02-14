package com.syntifi.ori.chains.eth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.dto.BlockDTO;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.web3j.protocol.Web3j;

@SpringBatchTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = EthChainCrawlerApplication.class)
@TestPropertySource("classpath:application.properties")
public class EthChainCrawlerJobTest {

    @Autowired
    public OriClient oriClient;

    @Autowired
    public Web3j service;

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

        // TODO: Improve comparison input/output
        BlockDTO oriBlock = oriClient.getLastBlock(oriChainConfigProperties.getChainTokenSymbol());
        //assertNotNull(service.getBlock(oriBlock.getHash()));
    }
    // TODO: Create test for batchSize = 1

    // TODO: Create test for BLOCK CONFLICT (save one item that already exists)

    // TODO: Create test for TRANSACTION CONFLICT (save one item that already
    // exists)

    // TODO: Create test for ACCOUNT CREATION

    // TODO: Create test for ACCOUNT CONFLICT (save one item that already exists)
}
