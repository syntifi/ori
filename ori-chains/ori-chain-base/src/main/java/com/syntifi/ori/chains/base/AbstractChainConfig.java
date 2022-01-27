package com.syntifi.ori.chains.base;

import java.io.IOException;

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
public abstract class AbstractChainConfig<S> {

    public abstract int getChunkSize();

    public abstract String getOriHost();

    public abstract String getTokenSymbol();

    public abstract String getTokenName();

    public abstract String getTokenProtocol();

    public abstract String getBlockZero();

    @Bean
    public OriRestClient oriRestClient() {
        return new OriRestClient(getOriHost());
    }

    @Bean
    public abstract S service() throws IOException;
}