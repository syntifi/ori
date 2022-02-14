package com.syntifi.ori.chains.eth;

import java.io.IOException;

import com.syntifi.ori.chains.base.OriChainConfig;
import com.syntifi.ori.chains.base.client.MockOriClient;
import com.syntifi.ori.client.OriClient;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;

@Configuration
public class EthTestChainConfig extends OriChainConfig {

    @Bean
    protected OriClient getOriClient() {
        return new MockOriClient();
    }

    @Bean
    protected Web3j getServiceInstance() throws IOException {
        return Mockito.mock(Web3j.class);
    }
}
