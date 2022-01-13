package com.syntifi.ori.chains.cspr;

import java.net.MalformedURLException;

import com.syntifi.casper.sdk.service.CasperService;
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

    @Value("${cspr.node}")
    private String csprNode;

    @Value("${cspr.port}")
    private int csprPort;

    @Bean
    public OriRestClient oriRestClient() {
        return new OriRestClient(oriHost);
    }

    @Bean
    public CasperService casperService() throws MalformedURLException {
        return CasperService.usingPeer(csprNode, csprPort);
    }
}