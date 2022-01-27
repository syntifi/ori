package com.syntifi.ori.chains.cspr;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.AbstractChainCrawlerApplication;
import com.syntifi.ori.chains.cspr.model.CsprChainBlockAndTransfers;
import com.syntifi.ori.chains.cspr.processor.CsprChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.cspr.reader.CsprChainBlockAndTransfersReader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableBatchProcessing
@Configuration
@ComponentScan
public class CsprChainCrawlerApplication extends AbstractChainCrawlerApplication<CasperService, CsprChainBlockAndTransfers> {
    @Autowired
    private CsprChainConfig chainConfig;

    @Autowired
    private CsprChainBlockAndTransfersReader blockAndTransfersReader;

    @Autowired
    private CsprChainBlockAndTransfersProcessor blockAndTransfersProcessor;

    public static void main(String[] args) {
        SpringApplication.run(CsprChainCrawlerApplication.class, args);
    }
}