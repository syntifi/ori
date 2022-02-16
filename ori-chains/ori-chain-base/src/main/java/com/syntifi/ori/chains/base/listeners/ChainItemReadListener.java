package com.syntifi.ori.chains.base.listeners;

import com.syntifi.ori.chains.base.model.ChainData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.OnReadError;

public class ChainItemReadListener<T extends ChainData<?, ?>> {
    
    protected static final Log logger = LogFactory.getLog(ChainItemReadListener.class);

    @BeforeRead
    public void beforeRead() {
        logger.info("Reading next block");
    }

    @AfterRead
    public void afterRead(T item) {
        logger.info("Next block read");
    }

    @OnReadError
    public void onReadError(Exception e) {
        logger.error(String.format("Error reading next block %s", e.getMessage()));
    }
}