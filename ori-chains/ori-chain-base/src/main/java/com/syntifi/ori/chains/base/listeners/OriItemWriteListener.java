package com.syntifi.ori.chains.base.listeners;

import java.util.List;

import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.annotation.OnWriteError;

public class OriItemWriteListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeWrite
    public void beforeWrite(List<OriBlockAndTransfers> items) {
        logger.info("OriItemWriteListener - beforeWrite");
    }

    @AfterWrite
    public void afterWrite(List<OriBlockAndTransfers> items) {
        logger.info("OriItemWriteListener - afterWrite");
    }

    @OnWriteError
    public void onWriteError(Exception exception, List<OriBlockAndTransfers> items) {
        logger.info("OriItemWriteListener - onWriteError");
    }
}