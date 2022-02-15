package com.syntifi.ori.chains.base.reader;

import java.io.IOException;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.model.ChainData;
import com.syntifi.ori.client.OriClient;

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
public abstract class AbstractChainReader<S, T extends ChainData<?, ?>>
        implements ItemReader<T> {

    private Long blockHeight;
    private S chainService;
    private OriClient oriClient;
    private OriChainConfigProperties oriChainConfigProperties;

    protected AbstractChainReader(S chainService,
            OriClient oriClient,
            OriChainConfigProperties oriChainConfigProperties) {
        this.chainService = chainService;
        this.oriClient = oriClient;
        this.oriChainConfigProperties = oriChainConfigProperties;
        initialize();
    }

    private void initialize() {
        try {
            blockHeight = oriClient.getLastBlock(oriChainConfigProperties.getChainTokenSymbol()).getHeight();
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                blockHeight = oriChainConfigProperties.getChainBlockZeroHeight();
            }
        }
    }

    protected boolean nextItem() {
        blockHeight += 1;
        return true;
    }

    // read() should return null if next item is not found
    @Override
    public abstract T read() throws IOException, InterruptedException;
}
