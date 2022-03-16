package com.syntifi.ori.chains.base.processor;

import java.text.SimpleDateFormat;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.model.OriData;

import org.springframework.batch.item.ItemProcessor;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Abstract class that implements {@link ItemProcessor} to be extended by target
 * chain crawler and process the
 * input from chain to ORI compliant format
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public abstract class AbstractChainProcessor<T> implements ItemProcessor<T, OriData> {

    protected final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * {@link OriChainConfigProperties} reference
     */
    @Getter(value = AccessLevel.PROTECTED)
    protected final OriChainConfigProperties oriChainConfigProperties;

    /**
     * Constructor for injecting {@link OriChainConfigProperties}
     * 
     * @param oriChainConfigProperties the {@link OriChainConfigProperties} object
     */
    protected AbstractChainProcessor(OriChainConfigProperties oriChainConfigProperties) {
        this.oriChainConfigProperties = oriChainConfigProperties;
    }
}
