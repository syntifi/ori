package com.syntifi.ori.chains.eth;

import com.syntifi.ori.client.OriRestClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@ComponentScan
public class EthConfig {

    @Value("${ori.host}")
    private String oriHost;

    @Value("${eth.node}")
    private String ethNode;

    @Value("${eth.port}")
    private int ethPort;

    @Bean
    public OriRestClient oriRestClient() {
        return new OriRestClient(oriHost);
    }

    @Bean
    public Web3j ethService() {
        return Web3j.build(new HttpService("http://" + ethNode + ":" + ethPort));
    }
}