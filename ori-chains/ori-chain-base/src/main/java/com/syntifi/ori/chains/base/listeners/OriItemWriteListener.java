package com.syntifi.ori.chains.base.listeners;

import java.util.List;

import com.syntifi.ori.chains.base.model.OriData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.annotation.OnWriteError;

public class OriItemWriteListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OriItemWriteListener.class);

    @BeforeWrite
    public void beforeWrite(List<OriData> items) {
        LOGGER.debug("Writing next blocks");
    }

    @AfterWrite
    public void afterWrite(List<OriData> items) {
        LOGGER.debug("Next blocks written");
    }

    @OnWriteError
    public void onWriteError(Exception e, List<OriData> items) {
        LOGGER.error("Error writing next blocks: {}", e.getMessage());
    }
}