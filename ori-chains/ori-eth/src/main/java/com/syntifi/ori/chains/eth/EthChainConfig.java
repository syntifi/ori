package com.syntifi.ori.chains.eth;

import com.syntifi.ori.chains.base.OriChainConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * {@link OriChainConfig} for Ethereum block chain
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Configuration
public class EthChainConfig extends OriChainConfig {

    @Bean
    protected Web3j getServiceInstance() {
        return Web3j.build(
                new HttpService(String.format("%s://%s:%s",
                        getOriChainConfigProperties().getChain().getNode().getScheme(),
                        getOriChainConfigProperties().getChain().getNode().getAddress(),
                        getOriChainConfigProperties().getChain().getNode().getPort())));
    }
}
