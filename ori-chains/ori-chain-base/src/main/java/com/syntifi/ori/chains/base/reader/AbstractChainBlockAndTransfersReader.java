package com.syntifi.ori.chains.base.reader;

import java.io.IOException;

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
 * @author Andre Bertoalce <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractChainBlockAndTransfersReader<S, T extends ChainBlockAndTransfers<?, ?>>
        implements ItemReader<T> {

    private Long blockHeight;
    private String tokenSymbol;
    private S chainService;
    private OriRestClient oriRestClient;

    protected AbstractChainBlockAndTransfersReader(S chainService,
            OriRestClient oriRestClient,
            String tokenSymbol) {
        this.chainService = chainService;
        this.oriRestClient = oriRestClient;
        this.tokenSymbol = tokenSymbol;
        initialize();
    }

    private void initialize() {
        try {
            blockHeight = oriRestClient.getLastBlock(tokenSymbol).getHeight() + 1;
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                blockHeight = 0L;
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
