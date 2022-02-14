package com.syntifi.ori.chains.eth;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBatchTest
@ExtendWith(SpringExtension.class)
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
