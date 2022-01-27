package com.syntifi.ori.chains.base.listeners;

import com.syntifi.ori.chains.base.model.ChainBlockAndTransfers;
import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.core.annotation.OnProcessError;

public class ChainItemProcessListener<T extends ChainBlockAndTransfers<?, ?>> {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeProcess
    public void beforeProcess(T item) {
        logger.info("ChainItemProcessListener - beforeProcess");
    }

    @AfterProcess
    public void afterProcess(T item, OriBlockAndTransfers result) {
        logger.info("ChainItemProcessListener - afterProcess");
    }

    @OnProcessError
    public void onProcessError(T item, Exception e) {
        logger.info("ChainItemProcessListener - onProcessError");
    }
}