package com.syntifi.ori.chains.eth.reader;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.syntifi.ori.chains.eth.EthChainCrawlerJob;
import com.syntifi.ori.chains.eth.EthTestChainConfig;
import com.syntifi.ori.chains.eth.model.EthChainData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
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
public class EthChainReaderTest {

    private final static int MAX_HEIGHT = 10;
    private int currentHeight = 0;

    @Autowired
    private Web3j service;

    @Autowired
    private EthChainReader reader;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    public void testReader_once() throws IOException, InterruptedException {
        when(service.ethGetBlockByNumber(any(DefaultBlockParameter.class), eq(true)))
                .thenAnswer(i -> i.callRealMethod());

        EthChainData chainData = reader.read();
        assertNotNull(chainData);
    }

    @Test
    public void testReader_MAX_HEIGHT_times() throws IOException, InterruptedException {
        @SuppressWarnings("unchecked")
        Request<?, EthBlock> mockRequest = (Request<?, EthBlock>) Mockito.mock(Request.class);

        // When MAX_HEIGHT is reached, return a mockRequest...
        when(service.ethGetBlockByNumber(any(DefaultBlockParameter.class), eq(true))).thenAnswer(i -> {
            return currentHeight < MAX_HEIGHT ? i.callRealMethod() : mockRequest;
        });

        // ... which will return a null Block inside the response and end the reader
        when(mockRequest.send()).thenAnswer(i -> {
            return new EthBlock();
        });

        EthChainData chainData;
        while ((chainData = reader.read()) != null) {
            assertNotNull(chainData);
            currentHeight++;
        }
        assertNull(chainData);
    }
}
