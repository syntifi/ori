package com.syntifi.ori.chains.base.listeners;

import com.syntifi.ori.chains.base.model.ChainBlockAndTransfers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.OnReadError;

public class ChainItemReadListener<T extends ChainBlockAndTransfers<?, ?>> {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeRead
    public void beforeRead() {
        logger.debug("Reading next block");
    }

    @AfterRead
    public void afterRead(T item) {
        logger.debug("[{}] Next block read", item.getChainBlock().getClass().getSimpleName());
    }

    @OnReadError
    public void onReadError(Exception e) {
        logger.error("Error reading next block {}", e.getMessage());
    }
}