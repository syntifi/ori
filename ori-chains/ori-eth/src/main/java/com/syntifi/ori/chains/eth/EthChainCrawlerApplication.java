package com.syntifi.ori.chains.eth;

import com.syntifi.ori.chains.base.AbstractChainCrawlerApplication;
import com.syntifi.ori.chains.eth.model.EthChainBlockAndTransfers;
import com.syntifi.ori.chains.eth.processor.EthChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.eth.reader.EthChainBlockAndTransfersReader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import lombok.Getter;

@Getter
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableBatchProcessing
@Configuration
@ComponentScan
@Component
public class EthChainCrawlerApplication extends AbstractChainCrawlerApplication<Web3j, EthChainBlockAndTransfers> {
    @Autowired
    private EthChainConfig chainConfig;

    @Autowired
    private EthChainBlockAndTransfersReader blockAndTransfersReader;

    @Autowired
    private EthChainBlockAndTransfersProcessor blockAndTransfersProcessor;

    public static void main(String[] args) {
        SpringApplication.run(EthChainCrawlerApplication.class, args);
    }
}