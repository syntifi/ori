package com.syntifi.ori.chains.eth;

import com.syntifi.ori.chains.base.OriChainConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class EthChainConfig extends OriChainConfig {
    @Bean
    protected Web3j getServiceInstance() {
        return Web3j.build(
                new HttpService("http://" + getOriChainConfigProperties().getChainNode() + ":"
                        + getOriChainConfigProperties().getChainNodePort()));
    }
}
