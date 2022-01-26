package com.syntifi.ori.chains.cspr;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.cspr.listeners.CustomChunkListener;
import com.syntifi.ori.chains.cspr.listeners.JobResultListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemProcessListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemReadListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemWriteListener;
import com.syntifi.ori.chains.cspr.listeners.StepResultListener;
import com.syntifi.ori.chains.cspr.model.CsprBlockAndTransfers;
import com.syntifi.ori.chains.cspr.model.OriBlockAndTransfers;
import com.syntifi.ori.chains.cspr.processor.BlockAndTransfersProcessor;
import com.syntifi.ori.chains.cspr.reader.BlockAndTransfersReader;
import com.syntifi.ori.chains.cspr.writer.BlockAndTransfersWriter;
import com.syntifi.ori.client.OriRestClient;
import com.syntifi.ori.model.OriBlockPost;
import com.syntifi.ori.model.OriToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableBatchProcessing
@Configuration
@ComponentScan
public class CsprCrawlerApplication {
    private static Logger LOGGER = LoggerFactory.getLogger(CsprCrawlerApplication.class);

    @Value("${cspr.token}")
    private String tokenSymbol;

    @Value("${cspr.token.name}")
    private String tokenName;

    @Value("${cspr.token.protocol}")
    private String tokenProtocol;

    @Value("${cspr.block.zero}")
    private String blockZero;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private OriRestClient oriRestClient;

    @Autowired
    private CasperService casperService;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private void createTokenIfNeeded() {
        try {
            oriRestClient.getToken(tokenSymbol);
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                OriToken token = new OriToken();
                token.setSymbol(tokenSymbol);
                token.setName(tokenName);
                token.setProtocol(tokenProtocol);
                oriRestClient.postToken(token);
            }
        }
    }

    private void addBlockZeroIfNeeded() {
        try {
            oriRestClient.getBlock(tokenSymbol, blockZero);
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                OriBlockPost block = new OriBlockPost();
                block.setTimeStamp("2000-01-01T00:00:00.000+0000");
                block.setHash(blockZero);
                block.setHeight(-1L);
                block.setEra(-1L);
                block.setRoot(blockZero);
                block.setValidator(blockZero);
                oriRestClient.postBlock(tokenSymbol, "null", block);
            }
        }
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<CsprBlockAndTransfers, OriBlockAndTransfers>chunk(1)
                .reader(new BlockAndTransfersReader(casperService,
                        oriRestClient,
                        tokenSymbol))
                .processor(new BlockAndTransfersProcessor())
                .writer(new BlockAndTransfersWriter(oriRestClient,
                        tokenSymbol))
                .listener(new StepResultListener())
                .listener(new CustomChunkListener())
                .listener(new StepItemReadListener())
                .listener(new StepItemProcessListener())
                .listener(new StepItemWriteListener())
                .build();
    }

    @Bean
    public Job getBlockAndTransfersAndWrite() {
        createTokenIfNeeded();
        addBlockZeroIfNeeded();
        return jobBuilderFactory.get("getBlockAndTransfersAndWrite")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(step1())
                // .next(stepTwo())
                .build();
    }

    public static void main(String[] args) {
        LOGGER.debug("Starting {}", CsprCrawlerApplication.class.getSimpleName());
        SpringApplication.run(CsprCrawlerApplication.class, args);
    }
}