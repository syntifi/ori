package com.syntifi.ori.chains.cspr;

import java.io.IOException;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.OriChainConfigProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsprChainConfig {
    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    @Bean
    protected CasperService getServiceInstance() throws IOException {
        return CasperService.usingPeer(oriChainConfigProperties.getChainNode(), oriChainConfigProperties.getChainNodePort());
    }
}
