package com.syntifi.ori.chains.eth.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigInteger;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.chains.eth.AbstractEthChainTest;
import com.syntifi.ori.chains.eth.EthChainCrawlerJob;
import com.syntifi.ori.chains.eth.EthTestChainConfig;
import com.syntifi.ori.chains.eth.model.EthChainData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;

@SpringBatchTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { EthTestChainConfig.class, EthChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class EthChainProcessorTest extends AbstractEthChainTest {

    protected static final Log logger = LogFactory.getLog(EthChainProcessorTest.class);

    @Autowired
    private EthChainProcessor processor;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    public void testProcessor(@Autowired Web3j service) throws Exception {
        int decimalHeight = 4326518;
        // test all json blocks on resources/test-data
        while (true) {
            BigInteger height = BigInteger.valueOf(decimalHeight++);
            DefaultBlockParameter blockParam = DefaultBlockParameter.valueOf(height);
            Request<?, EthBlock> blockRequest = service.ethGetBlockByNumber(blockParam, true);
            EthBlock block = blockRequest.send();

            if (block == null || block.getBlock() == null) {
                break;
            }

            EthChainData chainData = EthChainData.builder()
                    .chainBlock(block)
                    .chainTransfers(block.getResult().getTransactions()
                            .stream()
                            .map(transaction -> (TransactionObject) transaction.get())
                            .collect(Collectors.toList()))
                    .build();

            OriData oriData = processor.process(chainData);

            assertNotNull(oriData);
        }
    }
}
