package com.syntifi.ori.chains.eth;

import com.syntifi.ori.chains.base.AbstractChainCrawlerJob;
import com.syntifi.ori.chains.eth.model.EthChainData;
import com.syntifi.ori.chains.eth.processor.EthChainProcessor;
import com.syntifi.ori.chains.eth.reader.EthChainReader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.web3j.protocol.Web3j;

import lombok.Getter;

@Getter
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableBatchProcessing
public class EthChainCrawlerApplication extends AbstractChainCrawlerJob<Web3j, EthChainData> {
    @Autowired
    private EthChainConfig chainConfig;

    @Autowired
    private EthChainReader chainReader;

    @Autowired
    private EthChainProcessor chainProcessor;

    public static void main(String[] args) {
        SpringApplication.run(EthChainCrawlerApplication.class, args);
    }
}