package com.syntifi.ori.chains.cspr;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runner for Casper Chain Crawler Job
 */
@SpringBootApplication
@EnableBatchProcessing
public class CsprChainCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsprChainCrawlerApplication.class, args);
    }
}