package com.syntifi.ori.chains.base.reader;

import java.io.IOException;

import com.syntifi.ori.chains.base.AbstractChainConfig;
import com.syntifi.ori.chains.base.model.ChainBlockAndTransfers;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.batch.item.ItemReader;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Abstract class to be extended by target chain crawler and read the
 * input from chain
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertolace <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractChainBlockAndTransfersReader<S, T extends ChainBlockAndTransfers<?, ?>>
        implements ItemReader<T> {

    private Long blockHeight;
    private S chainService;
    private OriRestClient oriRestClient;
    private AbstractChainConfig<S> chainConfig;

    protected AbstractChainBlockAndTransfersReader(S chainService,
            OriRestClient oriRestClient,
            AbstractChainConfig<S> chainConfig) {
        this.chainService = chainService;
        this.oriRestClient = oriRestClient;
        this.chainConfig = chainConfig;
        initialize();
    }

    private void initialize() {
        try {
            blockHeight = oriRestClient.getLastBlock(chainConfig.getTokenSymbol()).getHeight() + 1;
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                blockHeight = chainConfig.getBlockZeroHeight() + 1;
            }
        }
    }

    protected boolean nextItem() {
        blockHeight += 1;
        return true;
    }

    // READ should return null if next item is not found
    @Override
    public abstract T read() throws IOException, InterruptedException;
}
