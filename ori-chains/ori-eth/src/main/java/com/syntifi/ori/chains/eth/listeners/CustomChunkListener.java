package com.syntifi.ori.chains.eth.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class CustomChunkListener implements ChunkListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

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