package com.syntifi.ori.chains.cspr.listeners;

import java.util.logging.Logger;

import org.springframework.batch.core.ItemReadListener;

public class StepItemReadListener implements ItemReadListener<String> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void beforeRead() {
        logger.info("ItemReadListener - beforeRead");
    }

    @Override
    public void afterRead(String item) {
        logger.info("ItemReadListener - afterRead");
    }

    @Override
    public void onReadError(Exception ex) {
        logger.info("ItemReadListener - onReadError");
    }
}