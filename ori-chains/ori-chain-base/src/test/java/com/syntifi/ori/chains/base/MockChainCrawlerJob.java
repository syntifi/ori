package com.syntifi.ori.chains.base;

import com.syntifi.ori.chains.base.client.MockChainService;
import com.syntifi.ori.chains.base.model.MockChainData;
import com.syntifi.ori.chains.base.processor.MockChainProcessor;
import com.syntifi.ori.chains.base.reader.MockChainReader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.Getter;

@Getter
@SpringBootApplication
@EnableBatchProcessing
public class MockChainCrawlerJob extends AbstractChainCrawlerJob<MockChainService, MockChainData> {
    @Autowired
    private MockChainConfig chainConfig;

    @Autowired
    private MockChainReader chainReader;

    @Autowired
    private MockChainProcessor chainProcessor;

    public static void main(String[] args) {
        SpringApplication.run(MockChainCrawlerJob.class, args);
    }
}
