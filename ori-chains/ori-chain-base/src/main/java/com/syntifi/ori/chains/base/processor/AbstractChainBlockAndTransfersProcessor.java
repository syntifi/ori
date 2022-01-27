package com.syntifi.ori.chains.base.processor;

import java.text.SimpleDateFormat;

import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;

import org.springframework.batch.item.ItemProcessor;

/**
 * Abstract class to be extended by target chain crawler and process the
 * input from chain to ORI compliant format
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertoalce <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
public abstract class AbstractChainBlockAndTransfersProcessor<T> implements ItemProcessor<T, OriBlockAndTransfers> {

    protected final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

}
