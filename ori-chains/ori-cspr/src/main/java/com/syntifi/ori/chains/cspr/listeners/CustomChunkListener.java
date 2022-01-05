package com.syntifi.ori.chains.cspr.listeners;

import java.util.logging.Logger;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class CustomChunkListener implements ChunkListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void afterChunk(ChunkContext context) {
        logger.info("Called afterChunk().");
    }

    @Override
    public void beforeChunk(ChunkContext context) {
        logger.info("Called beforeChunk().");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        logger.info("Called afterChunkError().");
    }
}