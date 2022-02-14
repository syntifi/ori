package com.syntifi.ori.chains.base;

import java.io.IOException;

import com.syntifi.ori.chains.base.client.MockOriClient;
import com.syntifi.ori.chains.base.client.MockTestChainService;
import com.syntifi.ori.client.OriClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MockChainConfig {

    @Bean
    @Primary
    protected OriClient getOriClient() {
        return new MockOriClient();
    }

    @Bean
    protected MockTestChainService getServiceInstance() throws IOException {
        return new MockTestChainService();
    }
}
