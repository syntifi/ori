package com.syntifi.ori.chains.cspr;

import java.io.IOException;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.OriChainConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsprChainConfig extends OriChainConfig {

    @Bean
    protected CasperService getServiceInstance() throws IOException {
        return CasperService.usingPeer(getOriChainConfigProperties().getChainNode(),
                getOriChainConfigProperties().getChainNodePort());
    }
}
