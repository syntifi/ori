package com.syntifi.ori.chains.base.listeners;

import com.syntifi.ori.chains.base.model.ChainData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.OnReadError;

public class ChainItemReadListener<T extends ChainData<?, ?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChainItemReadListener.class);

    @BeforeRead
    public void beforeRead() {
        LOGGER.debug("Reading next block");
    }

    @AfterRead
    public void afterRead(T item) {
        LOGGER.debug("Next block read");
    }

    @OnReadError
    public void onReadError(Exception e) {
        LOGGER.error("Error reading next block {}", e.getMessage());
    }
}