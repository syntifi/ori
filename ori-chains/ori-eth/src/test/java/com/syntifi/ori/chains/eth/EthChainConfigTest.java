package com.syntifi.ori.chains.eth;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EthChainCrawlerApplication.class)
@TestPropertySource("classpath:application.properties")
public class EthChainConfigTest {
    @Autowired
    EthChainConfig ethChainConfig;

    @Test
    void serviceNotNull() {
        assertNotNull(ethChainConfig);
    }
}
