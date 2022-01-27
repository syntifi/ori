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
        logger.info("ItemReadListener - beforeRead");
    }

    @AfterRead
    public void afterRead(T item) {
        logger.info("ItemReadListener - afterRead");
    }

    @OnReadError
    public void onReadError(Exception ex) {
        logger.info("ItemReadListener - onReadError");
    }
}