package com.syntifi.ori.chains.base.listeners;

import java.util.List;

import com.syntifi.ori.chains.base.model.OriData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.annotation.OnWriteError;

/**
 * Chain WriteListener for listening to batch write events
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class OriItemWriteListener {

    protected static final Log logger = LogFactory.getLog(OriItemWriteListener.class);

    @BeforeWrite
    public void beforeWrite(List<OriData> items) {
        logger.info("Writing next blocks");
    }

    @AfterWrite
    public void afterWrite(List<OriData> items) {
        logger.info("Next blocks written");
    }

    @OnWriteError
    public void onWriteError(Exception e, List<OriData> items) {
        logger.error(String.format("Error writing next blocks: %s", e.getMessage()));
    }
}