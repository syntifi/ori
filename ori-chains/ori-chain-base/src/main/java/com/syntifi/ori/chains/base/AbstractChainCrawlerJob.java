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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.AccessLevel;
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
@EnableConfigurationProperties(OriChainConfigProperties.class)
public abstract class AbstractChainCrawlerJob<S, T extends ChainData<?, ?>> {
    protected static final Log logger = LogFactory.getLog(AbstractChainCrawlerJob.class);

    protected abstract AbstractChainProcessor<T> getChainProcessor();

    protected abstract AbstractChainReader<S, T> getChainReader();

    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriWriter oriWriter;

    protected abstract S getChainService();

    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriClient oriClient;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private void createTokenIfNeeded() {
        try {
            logger.info("Getting token from properties...");

            oriClient.getToken(oriChainConfigProperties.getChainTokenSymbol());

            logger.info(String.format("...found for value \"%s\".", oriChainConfigProperties.getChainTokenSymbol()));
        } catch (WebClientResponseException e) {
            logger.info(
                    String.format("...not found for value \"%s\".", oriChainConfigProperties.getChainTokenSymbol()),
                    e);

            if (e.getRawStatusCode() == 404) {
                logger.info(String.format("Token \"%s\" not found. Creating...",
                        oriChainConfigProperties.getChainTokenSymbol()));

                TokenDTO token = new TokenDTO();
                token.setSymbol(oriChainConfigProperties.getChainTokenSymbol());
                token.setName(oriChainConfigProperties.getChainTokenName());
                token.setProtocol(oriChainConfigProperties.getChainTokenProtocol());
                oriClient.postToken(token);

                logger.info(String.format("...token \"%s\" created!", oriChainConfigProperties.getChainTokenSymbol()));
            }
        }
    }

    private void addBlockZeroIfNeeded() {
        try {
            logger.info(String.format("Retrieving zero hash block for \"%s\"...",
                    oriChainConfigProperties.getChainTokenSymbol()));

            oriClient.getBlock(oriChainConfigProperties.getChainTokenSymbol(),
                    oriChainConfigProperties.getChainBlockZeroHash());

            logger.info(String.format("...zero hash block found for \"%s\" with hash \"%s\" and height \"%s\".",
                    oriChainConfigProperties.getChainTokenSymbol(), oriChainConfigProperties.getChainBlockZeroHash(),
                    oriChainConfigProperties.getChainBlockZeroHeight()));
        } catch (WebClientResponseException e) {
            logger.info(
                    String.format("...not found for token \"%s\".", oriChainConfigProperties.getChainTokenSymbol()), e);

            if (e.getRawStatusCode() == 404) {
                logger.info(String.format("Zero hash block for token \"%s\" not found. Creating...",
                        oriChainConfigProperties.getChainTokenSymbol()));

                BlockDTO block = new BlockDTO();
                block.setTimeStamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("GMT")));
                block.setHash(oriChainConfigProperties.getChainBlockZeroHash());
                block.setHeight(oriChainConfigProperties.getChainBlockZeroHeight());
                block.setEra(-1L);
                block.setRoot(oriChainConfigProperties.getChainBlockZeroHash());
                block.setValidator(oriChainConfigProperties.getChainBlockZeroHash());
                oriClient.postBlock(oriChainConfigProperties.getChainTokenSymbol(), block);

                logger.info(String.format("...zero hash block for token \"%s\" and hash \"%s\" created!",
                        oriChainConfigProperties.getChainTokenSymbol(),
                        oriChainConfigProperties.getChainBlockZeroHash()));
            }
        }
    }

    @Bean
    public Step crawlAndSendToOri() {
        logger.info(String.format("Registering step \"crawlAndSendToOri\" for \"%s\"", getClass().getSimpleName()));

        return stepBuilderFactory.get("crawlAndSendToOri")
                .<T, OriData>chunk(oriChainConfigProperties.getBatchChunkSize())
                .reader(getChainReader())
                .processor(getChainProcessor())
                .writer(oriWriter)
                .listener(new OriStepExecutionListener())
                .listener(new OriChunkListener())
                .listener(new ChainItemReadListener<T>())
                .listener(new ChainItemProcessListener<T>())
                .listener(new OriItemWriteListener())
                .build();
    }

    @Bean
    public Job oriCrawlerJob() {
        createTokenIfNeeded();
        addBlockZeroIfNeeded();

        logger.info(String.format("Registering job \"oriCrawlerJob\" for \"%s\"", getClass().getSimpleName()));

        return jobBuilderFactory.get(getClass().getSimpleName())
                .incrementer(new RunIdIncrementer())
                .listener(new OriJobExecutionListener())
                .start(crawlAndSendToOri())
                .build();
    }
}