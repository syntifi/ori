package com.syntifi.ori.chains.base.listeners;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

public class OriChunkListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private BigDecimal processedItems = BigDecimal.ZERO;
    private final BigDecimal tenBigDecimal = new BigDecimal(10);

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        logger.info("OriChunkListener - afterChunk");
        processedItems = processedItems.add(BigDecimal.ONE);
        if (processedItems.remainder(tenBigDecimal) == BigDecimal.ZERO) {
            logger.info("{} items processed.", processedItems);
        }
    }

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        logger.info("OriChunkListener - beforeChunk");
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext context) {
        logger.info("OriChunkListener - afterChunkError");
    }
}