package com.syntifi.ori.chains.base.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

public class OriChunkListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OriChunkListener.class);

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        LOGGER.debug("Processing next chunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        LOGGER.debug("Chunk processed.");
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext context) {
        LOGGER.error("Chunk error!");
    }
}