package com.syntifi.ori.chains.base.model;

import java.util.List;

/**
 * Interface to be implemented by crawlers with types Block (B) and Transaction
 * (T) of target chain types
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public interface ChainData<B, T> {

    /**
     * getter method for target chain block
     * 
     * @return the target chain typed block object
     */
    public B getChainBlock();

    /**
     * getter for list of target chain transactions
     * 
     * @return the target chain list of typed transaction objects
     */
    public List<T> getChainTransfers();
}
