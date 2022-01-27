package com.syntifi.ori.chains.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.syntifi.ori.chains.base.listeners.ChainItemProcessListener;
import com.syntifi.ori.chains.base.listeners.ChainItemReadListener;
import com.syntifi.ori.chains.base.listeners.OriChunkListener;
import com.syntifi.ori.chains.base.listeners.OriItemWriteListener;
import com.syntifi.ori.chains.base.listeners.OriJobExecutionListener;
import com.syntifi.ori.chains.base.listeners.OriStepExecutionListener;
import com.syntifi.ori.chains.base.model.ChainBlockAndTransfers;
import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;
import com.syntifi.ori.chains.base.processor.AbstractChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.base.reader.AbstractChainBlockAndTransfersReader;
import com.syntifi.ori.chains.base.writer.OriBlockAndTransfersWriter;
import com.syntifi.ori.client.OriRestClient;
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
public abstract class AbstractChainCrawlerApplication<S, T extends ChainBlockAndTransfers<?, ?>> {

    protected abstract AbstractChainConfig<S> getChainConfig();

    protected abstract AbstractChainBlockAndTransfersProcessor<T> getBlockAndTransfersProcessor();

    protected abstract AbstractChainBlockAndTransfersReader<S, T> getBlockAndTransfersReader();

    @Getter
    @Autowired
    private OriRestClient oriRestClient;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private void createTokenIfNeeded() {
        try {
            oriRestClient.getToken(getChainConfig().getTokenSymbol());
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                TokenDTO token = new TokenDTO();
                token.setSymbol(getChainConfig().getTokenSymbol());
                token.setName(getChainConfig().getTokenName());
                token.setProtocol(getChainConfig().getTokenProtocol());
                oriRestClient.postToken(token);
            }
        }
    }

    private void addBlockZeroIfNeeded() {
        try {
            oriRestClient.getBlock(getChainConfig().getTokenSymbol(), getChainConfig().getBlockZeroHash());
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                BlockDTO block = new BlockDTO();
                // TODO: fix this
                try {
                    block.setTimeStamp(dateFormatter.parse("1970-01-01T00:00:00.000+0000"));
                } catch (ParseException pe) {
                    throw new RuntimeException("Invalid timestamp for block zero");
                }
                block.setHash(getChainConfig().getBlockZeroHash());
                block.setHeight(getChainConfig().getBlockZeroHeight());
                block.setEra(-1L);
                block.setRoot(getChainConfig().getBlockZeroHash());
                block.setValidator(getChainConfig().getBlockZeroHash());
                oriRestClient.postBlock(getChainConfig().getTokenSymbol(), block);
            }
        }
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<T, OriBlockAndTransfers>chunk(getChainConfig().getChunkSize())
                .reader(getBlockAndTransfersReader())
                .processor(getBlockAndTransfersProcessor())
                .writer(new OriBlockAndTransfersWriter(oriRestClient, getChainConfig().getTokenSymbol()))
                .listener(new OriStepExecutionListener())
                .listener(new OriChunkListener())
                .listener(new ChainItemReadListener<T>())
                .listener(new ChainItemProcessListener<T>())
                .listener(new OriItemWriteListener())
                .build();
    }

    @Bean
    public Job getBlockAndTransfersAndWrite() {
        createTokenIfNeeded();
        addBlockZeroIfNeeded();
        return jobBuilderFactory.get("getBlockAndTransfersAndWrite")
                .incrementer(new RunIdIncrementer())
                .listener(new OriJobExecutionListener())
                .start(step1())
                // .next(stepTwo())
                .build();
    }
}