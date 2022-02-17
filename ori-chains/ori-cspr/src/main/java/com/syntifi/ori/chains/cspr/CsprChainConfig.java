package com.syntifi.ori.chains.cspr;

import java.net.MalformedURLException;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.OriChainConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link OriChainConfig} for Casper block chain
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Configuration
public class CsprChainConfig extends OriChainConfig {

    @Bean
    protected CasperService getServiceInstance() throws MalformedURLException {
        return CasperService.usingPeer(getOriChainConfigProperties().getChainNode(),
                getOriChainConfigProperties().getChainNodePort());
    }
}
