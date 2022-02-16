package com.syntifi.ori.chains.cspr.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.model.transfer.TransferData;
import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.chains.cspr.AbstractCsprChainTest;
import com.syntifi.ori.chains.cspr.CsprChainCrawlerJob;
import com.syntifi.ori.chains.cspr.CsprTestChainConfig;
import com.syntifi.ori.chains.cspr.model.CsprChainData;

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

@SpringBatchTest
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { CsprTestChainConfig.class, CsprChainCrawlerJob.class })
@TestPropertySource("classpath:application.properties")
public class CsprChainProcessorTest extends AbstractCsprChainTest {

    protected static final Log logger = LogFactory.getLog(CsprChainProcessorTest.class);

    @Autowired
    private CsprChainProcessor processor;

    public StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @Test
    public void testProcessor(@Autowired CasperService service) throws Exception {
        long decimalHeight = 553797L;
        // test all json blocks on resources/test-data
        while (true) {
            JsonBlockData blockData = service.getBlock(new HeightBlockIdentifier(decimalHeight));
            if (blockData == null || blockData.getBlock() == null) {
                break;
            }

            TransferData transferData = service.getBlockTransfers(new HeightBlockIdentifier(decimalHeight));

            CsprChainData chainData = CsprChainData.builder()
                    .chainBlock(blockData.getBlock())
                    .chainTransfers(transferData.getTransfers())
                    .build();

            OriData oriData = processor.process(chainData);

            assertNotNull(oriData);

            decimalHeight++;
        }
    }
}
