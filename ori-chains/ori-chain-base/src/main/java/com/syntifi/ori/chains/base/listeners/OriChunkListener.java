package com.syntifi.ori.chains.base.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * Chain ChunkListener for listening to batch chunk events
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class OriChunkListener {

    protected static final Log logger = LogFactory.getLog(OriChunkListener.class);

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        logger.info("Processing next chunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        logger.info("Chunk processed.");
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext context) {
        logger.error("Chunk error!");
    }
}