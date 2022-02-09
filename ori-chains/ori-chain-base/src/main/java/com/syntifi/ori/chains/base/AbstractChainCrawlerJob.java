package com.syntifi.ori.chains.base;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import com.syntifi.ori.chains.base.listeners.ChainItemProcessListener;
import com.syntifi.ori.chains.base.listeners.ChainItemReadListener;
import com.syntifi.ori.chains.base.listeners.OriChunkListener;
import com.syntifi.ori.chains.base.listeners.OriItemWriteListener;
import com.syntifi.ori.chains.base.listeners.OriJobExecutionListener;
import com.syntifi.ori.chains.base.listeners.OriStepExecutionListener;
import com.syntifi.ori.chains.base.model.ChainData;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.chains.base.processor.AbstractChainProcessor;
import com.syntifi.ori.chains.base.reader.AbstractChainReader;
import com.syntifi.ori.chains.base.writer.OriWriter;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TokenDTO;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.Getter;

/**
 * Abstract class to be extended by target chain crawler which effectively runs
 * the crawler
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertolace <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
public abstract class AbstractChainCrawlerJob<S, T extends ChainData<?, ?>> {

    protected abstract AbstractChainConfig<S> getChainConfig();

    protected abstract AbstractChainProcessor<T> getChainProcessor();

    protected abstract AbstractChainReader<S, T> getChainReader();

    @Getter
    @Autowired
    private OriClient oriClient;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private void createTokenIfNeeded() {
        try {
            oriClient.getToken(getChainConfig().getTokenSymbol());
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                TokenDTO token = new TokenDTO();
                token.setSymbol(getChainConfig().getTokenSymbol());
                token.setName(getChainConfig().getTokenName());
                token.setProtocol(getChainConfig().getTokenProtocol());
                oriClient.postToken(token);
            }
        }
    }

    private void addBlockZeroIfNeeded() {
        try {
            oriClient.getBlock(getChainConfig().getTokenSymbol(), getChainConfig().getBlockZeroHash());
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                BlockDTO block = new BlockDTO();
                block.setTimeStamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("GMT")));
                block.setHash(getChainConfig().getBlockZeroHash());
                block.setHeight(getChainConfig().getBlockZeroHeight());
                block.setEra(-1L);
                block.setRoot(getChainConfig().getBlockZeroHash());
                block.setValidator(getChainConfig().getBlockZeroHash());
                oriClient.postBlock(getChainConfig().getTokenSymbol(), block);
            }
        }
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<T, OriData>chunk(getChainConfig().getChunkSize())
                .reader(getChainReader())
                .processor(getChainProcessor())
                .writer(new OriWriter(oriClient, getChainConfig().getTokenSymbol()))
                .listener(new OriStepExecutionListener())
                .listener(new OriChunkListener())
                .listener(new ChainItemReadListener<T>())
                .listener(new ChainItemProcessListener<T>())
                .listener(new OriItemWriteListener())
                .build();
    }

    @Bean
    public Job job() {
        createTokenIfNeeded();
        addBlockZeroIfNeeded();
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(new OriJobExecutionListener())
                .start(step1())
                .build();
    }
}