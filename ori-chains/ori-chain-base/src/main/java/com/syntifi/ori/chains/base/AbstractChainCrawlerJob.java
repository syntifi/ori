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
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@EnableConfigurationProperties(OriChainConfigProperties.class)
public abstract class AbstractChainCrawlerJob<S, T extends ChainData<?, ?>> {

    protected static final Log logger = LogFactory.getLog(AbstractChainCrawlerJob.class);

    /**
     * Getter for chain processor
     * 
     * @return the actual chain processor
     */
    protected abstract AbstractChainProcessor<T> getChainProcessor();

    /**
     * Getter for chain reader
     * 
     * @return the actual chain reader
     */
    protected abstract AbstractChainReader<S, T> getChainReader();

    /**
     * Getter for chain service
     * 
     * @return the actual chain service
     */
    protected abstract S getChainService();

    /**
     * OriWriter instance
     * 
     * @return the oriWriter being used
     */
    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriWriter oriWriter;

    /**
     * {@link OriChainConfigProperties} reference
     * 
     * @return the {@link OriChainConfigProperties} object
     */
    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    /**
     * {@link OriClient} reference
     * 
     * @return the {@link OriClient} object
     */
    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriClient oriClient;

    /**
     * Spring batch {@link JobBuilderFactory}
     */
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    /**
     * Spring batch {@link StepBuilderFactory}
     */
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * Check if chain already exists on ORI and create if it does not
     */
    private void createChainIfNeeded() {
        try {
            logger.info("Getting chain from properties...");

            oriClient.getChain(oriChainConfigProperties.getChain().getTokenSymbol());

            logger.info(String.format("...found for value \"%s\".",
                    oriChainConfigProperties.getChain().getTokenSymbol()));
        } catch (WebClientResponseException e) {
            logger.info(
                    String.format("...not found for value \"%s\".",
                            oriChainConfigProperties.getChain().getTokenSymbol()),
                    e);

            if (e.getRawStatusCode() == 404) {
                logger.info(String.format("Token \"%s\" not found. Creating...",
                        oriChainConfigProperties.getChain().getTokenSymbol()));

                TokenDTO token = new TokenDTO();
                token.setSymbol(oriChainConfigProperties.getChain().getTokenSymbol());
                token.setName(oriChainConfigProperties.getChain().getTokenName());
                token.setChainName(oriChainConfigProperties.getChain().getTokenProtocol());
                token.check();
                oriClient.postToken(token.getChainName(), token);

                logger.info(String.format("...token \"%s\" created!",
                        oriChainConfigProperties.getChain().getTokenSymbol()));
            }
        }
    }
    /**
     * Check if token already exists on ORI and create if it does not
     */
    private void createTokenIfNeeded() {
        try {
            logger.info("Getting token from properties...");

            oriClient.getChain(oriChainConfigProperties.getChain().getTokenSymbol());

            logger.info(String.format("...found for value \"%s\".",
                    oriChainConfigProperties.getChain().getTokenSymbol()));
        } catch (WebClientResponseException e) {
            logger.info(
                    String.format("...not found for value \"%s\".",
                            oriChainConfigProperties.getChain().getTokenSymbol()),
                    e);

            if (e.getRawStatusCode() == 404) {
                logger.info(String.format("Token \"%s\" not found. Creating...",
                        oriChainConfigProperties.getChain().getTokenSymbol()));

                TokenDTO token = new TokenDTO();
                token.setSymbol(oriChainConfigProperties.getChain().getTokenSymbol());
                token.setName(oriChainConfigProperties.getChain().getTokenName());
                token.setChainName(oriChainConfigProperties.getChain().getTokenProtocol());
                token.check();
                oriClient.postToken(token.getChainName(), token);

                logger.info(String.format("...token \"%s\" created!",
                        oriChainConfigProperties.getChain().getTokenSymbol()));
            }
        }
    }

    /**
     * Check if the BLOCK ZERO exists already on ORI for this chain and writes it if
     * needed
     */
    private void addBlockZeroIfNeeded() {
        try {
            logger.info(String.format("Retrieving zero hash block for \"%s\"...",
                    oriChainConfigProperties.getChain().getTokenSymbol()));

            oriClient.getBlock(oriChainConfigProperties.getChain().getTokenSymbol(),
                    oriChainConfigProperties.getChain().getBlockZeroHash());

            logger.info(String.format(
                    "...zero hash block found for \"%s\" with hash \"%s\" and height \"%s\".",
                    oriChainConfigProperties.getChain().getTokenSymbol(),
                    oriChainConfigProperties.getChain().getBlockZeroHash(),
                    oriChainConfigProperties.getChain().getBlockZeroHeight()));
        } catch (WebClientResponseException e) {
            logger.info(
                    String.format("...not found for token \"%s\".",
                            oriChainConfigProperties.getChain().getTokenSymbol()),
                    e);

            if (e.getRawStatusCode() == 404) {
                logger.info(String.format("Zero hash block for token \"%s\" not found. Creating...",
                        oriChainConfigProperties.getChain().getTokenSymbol()));

                BlockDTO block = BlockDTO.builder()
                        .hash(oriChainConfigProperties.getChain().getBlockZeroHash())
                        .tokenSymbol(oriChainConfigProperties.getChain().getTokenSymbol())
                        .timeStamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("GMT")))
                        .height(oriChainConfigProperties.getChain().getBlockZeroHeight())
                        .era(-1L)
                        .root(oriChainConfigProperties.getChain().getBlockZeroHash())
                        .validator(oriChainConfigProperties.getChain().getBlockZeroHash())
                        .build();

                block.check();
                oriClient.postBlock(oriChainConfigProperties.getChain().getTokenSymbol(), block);

                logger.info(String.format(
                        "...zero hash block for token \"%s\" and hash \"%s\" created!",
                        oriChainConfigProperties.getChain().getTokenSymbol(),
                        oriChainConfigProperties.getChain().getBlockZeroHash()));
            }
        }
    }

    /**
     * The main step which crawls the node, extract, process and writes data to ORI
     * 
     * @return the bean of the step to be created
     */
    @Bean
    public Step crawlAndSendToOri() {
        logger.info(String.format("Registering step \"crawlAndSendToOri\" for \"%s\"",
                getClass().getSimpleName()));

        return stepBuilderFactory.get("crawlAndSendToOri")
                .<T, OriData>chunk(oriChainConfigProperties.getBatch().getChunkSize())
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

    /**
     * 
     * The job which runs the crawler
     * 
     * @return the bean of the job to be created
     */
    @Bean
    public Job oriCrawlerJob() {
        createChainIfNeeded();
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