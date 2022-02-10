package com.syntifi.ori.chains.base;

import java.io.IOException;

import com.syntifi.ori.chains.base.client.MockChainService;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.client.mock.MockOriClient;

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
    protected MockChainService getServiceInstance() throws IOException {
        return new MockChainService();
    }
}
