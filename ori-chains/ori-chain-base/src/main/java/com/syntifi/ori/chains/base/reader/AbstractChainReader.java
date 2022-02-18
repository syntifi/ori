package com.syntifi.ori.chains.base.reader;

import java.io.IOException;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.exception.OriChainCrawlerException;
import com.syntifi.ori.chains.base.model.ChainData;
import com.syntifi.ori.client.OriClient;

import org.springframework.batch.item.ItemReader;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Abstract class that implements {@link ItemReader} to be extended by target
 * chain crawler and read the
 * input from chain
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractChainReader<S, T extends ChainData<?, ?>>
        implements ItemReader<T> {

    /**
     * Current blockHeight
     * 
     * @return the current blockHeight
     */
    private Long blockHeight;

    /**
     * Chain service reference, injected by configuration
     * 
     * @return the chain service object
     */
    private S chainService;

    /**
     * {@link OriClient} reference
     * 
     * @return the {@link OriClient} object
     */
    private OriClient oriClient;

    /**
     * {@link OriChainConfigProperties} reference
     * 
     * @return the {@link OriChainConfigProperties} object
     */
    private OriChainConfigProperties oriChainConfigProperties;

    /**
     * Constructs an instance of an ItemReader for reading data from a blockchain
     * 
     * @param chainService             the chain service to be used by this
     *                                 ItemReader
     * @param oriClient                the ori client to be used by this ItemReader
     * @param oriChainConfigProperties the ori chain configuration
     */
    protected AbstractChainReader(S chainService,
            OriClient oriClient,
            OriChainConfigProperties oriChainConfigProperties) {
        this.chainService = chainService;
        this.oriClient = oriClient;
        this.oriChainConfigProperties = oriChainConfigProperties;
        initialize();
    }

    /**
     * Initializes the reader with the last used blockheight on database or reads
     * its value from properties
     */
    private void initialize() {
        try {
            blockHeight = oriClient.getLastBlock(oriChainConfigProperties.getChain().getTokenSymbol()).getHeight();
            blockHeight = blockHeight == -1 ? 0: blockHeight; // in case the zero block is the last one on database
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                blockHeight = 0L;
            } else {
                throw new OriChainCrawlerException("Error initializing blockHeight", e);
            }
        }
    }

    /**
     * Should be called each read call to prepare reader for the next iterations and
     * update state
     * 
     * @return true if should continue/false if should stop reading
     */
    protected boolean nextItem() {
        blockHeight += 1;
        return true;
    }

    /**
     * Abstract method to be implemented by each chain reader
     * read() should return null if next item is not found
     */
    @Override
    public abstract T read() throws IOException, InterruptedException;
}
