package com.syntifi.ori.chains.eth;

import com.syntifi.ori.chains.eth.listeners.CustomChunkListener;
import com.syntifi.ori.chains.eth.listeners.JobResultListener;
import com.syntifi.ori.chains.eth.listeners.StepItemProcessListener;
import com.syntifi.ori.chains.eth.listeners.StepItemReadListener;
import com.syntifi.ori.chains.eth.listeners.StepItemWriteListener;
import com.syntifi.ori.chains.eth.listeners.StepResultListener;
import com.syntifi.ori.chains.eth.model.OriBlock;
import com.syntifi.ori.chains.eth.processor.BlockAndTransfersProcessor;
import com.syntifi.ori.chains.eth.reader.BlockAndTransfersReader;
import com.syntifi.ori.chains.eth.writer.BlockAndTransfersWriter;
import com.syntifi.ori.client.OriRestClient;
import com.syntifi.ori.model.OriBlockPost;
import com.syntifi.ori.model.OriToken;

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
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableBatchProcessing
@Configuration
@ComponentScan
@Component
public class EthCrawlerApplication {

    @Value("${eth.token}")
    private String tokenSymbol;

    @Value("${eth.token.name}")
    private String tokenName;

    @Value("${eth.token.protocol}")
    private String tokenProtocol;

    @Value("${eth.block.zero}")
    private String blockZero;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private OriRestClient oriRestClient;

    @Autowired
    private Web3j ethService;

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
                block.setTimeStamp("1970-01-01T00:00:00.000+0000");
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
                .<EthBlock, OriBlock>chunk(1)
                .reader(new BlockAndTransfersReader(ethService,
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
        SpringApplication.run(EthCrawlerApplication.class, args);
    }
}