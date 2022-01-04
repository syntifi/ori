package com.syntifi.ori.chains.cspr.listeners;

import java.util.logging.Logger;

import org.springframework.batch.core.ItemProcessListener;

public class StepItemProcessListener implements ItemProcessListener<String, Number> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

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