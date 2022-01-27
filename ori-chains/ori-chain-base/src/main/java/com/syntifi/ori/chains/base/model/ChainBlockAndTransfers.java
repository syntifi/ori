package com.syntifi.ori.chains.base.model;

import java.util.List;

/**
 * Interface to be implemented by crawlers with types Block (B) and Transaction
 * (T) of target chain types
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertoalce <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
public interface ChainBlockAndTransfers<B, T> {

    public B getChainBlock();

    public List<T> getChainTransfers();
}
