package com.syntifi.ori.chains.eth;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Runner for {@link EthChainCrawlerJob}
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@SpringBootApplication
@EnableBatchProcessing
public class EthChainCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EthChainCrawlerApplication.class, args);
    }
}