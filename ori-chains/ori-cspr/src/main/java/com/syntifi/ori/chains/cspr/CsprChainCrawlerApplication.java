package com.syntifi.ori.chains.cspr;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.AbstractChainCrawlerJob;
import com.syntifi.ori.chains.cspr.model.CsprChainData;
import com.syntifi.ori.chains.cspr.processor.CsprChainProcessor;
import com.syntifi.ori.chains.cspr.reader.CsprChainReader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.Getter;

@Getter
@SpringBootApplication
@EnableBatchProcessing
public class CsprChainCrawlerApplication extends AbstractChainCrawlerJob<CasperService, CsprChainData> {
    @Autowired
    private CsprChainConfig chainConfig;

    @Autowired
    private CsprChainReader chainReader;

    @Autowired
    private CsprChainProcessor chainProcessor;

    public static void main(String[] args) {
        SpringApplication.run(CsprChainCrawlerApplication.class, args);
    }
}