package com.syntifi.ori.chains.cspr;

import com.syntifi.ori.client.OriRestClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class CsprConfig {

    @Value("${ori.host}")
    private String oriHost;

    @Bean
    public OriRestClient oriRestClient() {
        return new OriRestClient(oriHost);
    }
}