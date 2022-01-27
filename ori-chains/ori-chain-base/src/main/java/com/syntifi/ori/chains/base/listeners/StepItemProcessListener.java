package com.syntifi.ori.chains.base.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

public class StepItemProcessListener implements ItemProcessListener<String, Number> {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void beforeProcess(String item) {
        logger.info("ItemProcessListener - beforeProcess");
    }

    @Override
    public void afterProcess(String item, Number result) {
        logger.info("ItemProcessListener - afterProcess");
    }

    @Override
    public void onProcessError(String item, Exception e) {
        logger.info("ItemProcessListener - onProcessError");
    }
}