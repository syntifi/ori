package com.syntifi.ori.chains.base;

import java.io.IOException;

import com.syntifi.ori.chains.base.service.MockTestChainService;
import com.syntifi.ori.client.MockOriRestClient;
import com.syntifi.ori.client.OriClient;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@EnableAutoConfiguration
@Configuration
@ComponentScans(value = { @ComponentScan("com.syntifi.ori.chains.base") })
public class MockChainConfig extends OriChainConfig {

    @Bean
    protected OriClient getOriClient() {
        return new MockOriRestClient();
    }

    @Bean
    protected MockTestChainService getServiceInstance() throws IOException {
        return new MockTestChainService(getOriChainConfigProperties());
    }
}
