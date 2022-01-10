package com.syntifi.ori.chains.cspr;

import com.google.gson.JsonObject;
import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.ori.chains.cspr.listeners.CustomChunkListener;
import com.syntifi.ori.chains.cspr.listeners.JobResultListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemProcessListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemReadListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemWriteListener;
import com.syntifi.ori.chains.cspr.listeners.StepResultListener;
import com.syntifi.ori.chains.cspr.processor.BlockProcessor;
import com.syntifi.ori.chains.cspr.reader.BlockReader;
import com.syntifi.ori.chains.cspr.writer.BlockWriter;
import com.syntifi.ori.client.OriRestClient;

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

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableBatchProcessing
@Configuration
@ComponentScan
public class CsprCrawlerApplication {

    @Value("${cspr.token}")
    private String tokenSymbol;

    @Value("${cspr.token.name}")
    private String tokenName;

    @Value("${cspr.token.protocol}")
    private String tokenProtocol;

    @Value("${cspr.node}")
    private String csprNode;

    @Value("${cspr.port}")
    private int csprPort;

    @Value("${cspr.block.zero}")
    private String blockZero;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private OriRestClient oriRestClient;

    private void createTokenIfNeeded() {
        try {
            oriRestClient.getToken(tokenSymbol);
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                JsonObject tokenJson = new JsonObject();
                tokenJson.addProperty("symbol", tokenSymbol);
                tokenJson.addProperty("name", tokenName);
                tokenJson.addProperty("protocol", tokenProtocol);
                oriRestClient.postToken(tokenJson);
            }
        }
    }

    private void addBlockZeroIfNeeded() {
        try {
            oriRestClient.getBlock(tokenSymbol, blockZero);
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                JsonObject blockJson = new JsonObject();
                blockJson.addProperty("timeStamp", "2000-01-01T00:00:00.000+0000");
                blockJson.addProperty("hash", blockZero);
                blockJson.addProperty("height", 0);
                blockJson.addProperty("era", 0);
                blockJson.addProperty("root", blockZero);
                blockJson.addProperty("validator", blockZero);
                oriRestClient.postBlock(tokenSymbol, "null", blockJson);
            }
        }
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Step step1ReadBlock() {
        return stepBuilderFactory.get("step1ReadBlock")
                .<JsonBlock, JsonObject>chunk(1)
                .reader(new BlockReader(csprNode, csprPort))
                .processor(new BlockProcessor())
                .writer(new BlockWriter(oriRestClient, tokenSymbol))
                //.writer(new TransactionWriter(token))
                .listener(new StepResultListener())
                .listener(new CustomChunkListener())
                .listener(new StepItemReadListener())
                .listener(new StepItemProcessListener())
                .listener(new StepItemWriteListener())
                .build();
    }

    @Bean
    public Job getBlockTransactionAndWrite() {
        createTokenIfNeeded();
        addBlockZeroIfNeeded();
        return jobBuilderFactory.get("getBlockTransactionAndWrite")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(step1ReadBlock())
                // .next(stepTwo())
                .build();
    }
    public static void main(String[] args) {
        SpringApplication.run(CsprCrawlerApplication.class, args);
    }
}