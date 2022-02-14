package com.syntifi.ori.chains.base;

import java.io.IOException;

import com.syntifi.ori.chains.base.client.MockOriClient;
import com.syntifi.ori.chains.base.client.MockTestChainService;
import com.syntifi.ori.client.OriClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockChainConfig extends OriChainConfig {

    @Bean
    protected OriClient getOriClient() {
        return new MockOriClient();
    }

    @Bean
    protected MockTestChainService getServiceInstance() throws IOException {
        return new MockTestChainService(getOriChainConfigProperties());
    }
}
