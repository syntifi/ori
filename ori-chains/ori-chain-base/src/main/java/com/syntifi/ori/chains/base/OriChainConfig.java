package com.syntifi.ori.chains.base;

import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.Getter;

@Configuration
public class OriChainConfig {

    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    @Bean
    @ConditionalOnMissingBean
    protected OriClient getDefaultOriClient() {
        return new OriRestClient(oriChainConfigProperties.getHost());
    }
}
