package com.syntifi.ori.chains.eth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.dto.BlockDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;

@SpringBatchTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { EthTestChainConfig.class, EthChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class EthChainCrawlerJobTest implements InitializingBean {

    private final static int MAX_HEIGHT = 10;
    private int currentHeight = 0;

    @Autowired
    public OriClient oriClient;

    @Autowired
    public Web3j service;

    @Autowired
    public OriChainConfigProperties oriChainConfigProperties;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Override
    public void afterPropertiesSet() throws Exception {
        @SuppressWarnings("unchecked")
        Request<?, EthBlock> mockRequest = (Request<?, EthBlock>) Mockito.mock(Request.class);

        when(service.ethGetBlockByNumber(any(DefaultBlockParameter.class), eq(true))).thenAnswer(i -> {
            return ++currentHeight < MAX_HEIGHT ? i.callRealMethod() : mockRequest;
        });

        when(mockRequest.send()).thenAnswer(i -> {
            return new EthBlock();
        });
    }

    @Test
    void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();

        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());

        // TODO: Improve comparison input/output
        BlockDTO oriBlock = oriClient.getLastBlock(oriChainConfigProperties.getChainTokenSymbol());
        // assertNotNull(service.getBlock(oriBlock.getHash()));
    }
    // TODO: Create test for batchSize = 1

    // TODO: Create test for BLOCK CONFLICT (save one item that already exists)

    // TODO: Create test for TRANSACTION CONFLICT (save one item that already
    // exists)

    // TODO: Create test for ACCOUNT CREATION

    // TODO: Create test for ACCOUNT CONFLICT (save one item that already exists)
}
