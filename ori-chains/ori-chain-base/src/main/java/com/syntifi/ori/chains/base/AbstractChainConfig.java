package com.syntifi.ori.chains.base;

import java.io.IOException;

import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.context.annotation.Bean;

/**
 * Abstract class to be extended by target chain crawler and read the
 * specific configuration to feed the crawler
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertolace <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
public interface AbstractChainConfig<S> {

    public int getChunkSize();

    public String getOriHost();

    public String getTokenSymbol();

    public String getTokenName();

    public String getTokenProtocol();

    public String getBlockZeroHash();

    public long getBlockZeroHeight();

    @Bean
    public default OriClient oriClient() {
        return new OriRestClient(getOriHost());
    }

    @Bean
    public S service() throws IOException;
}