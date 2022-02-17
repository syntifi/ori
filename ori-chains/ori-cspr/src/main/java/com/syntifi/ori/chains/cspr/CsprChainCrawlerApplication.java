package com.syntifi.ori.chains.cspr;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runner for {@link CsprChainCrawlerJob}
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@SpringBootApplication
@EnableBatchProcessing
public class CsprChainCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsprChainCrawlerApplication.class, args);
    }
}