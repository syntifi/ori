package com.syntifi.ori.chains.base.listeners;

import java.math.BigDecimal;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

public class OriChunkListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        logger.debug("Processing next chunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        logger.debug("Chunk processed.");
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext context) {
        logger.error("Chunk error!");
    }
}