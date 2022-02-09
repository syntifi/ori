package com.syntifi.ori.chains.eth;

import com.syntifi.ori.chains.base.OriChainConfigProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class EthChainConfig {
    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    @Bean
    protected Web3j getServiceInstance() {
        return Web3j.build(
                new HttpService("http://" + oriChainConfigProperties.getChainNode() + ":" + oriChainConfigProperties.getChainNodePort()));
    }
}
