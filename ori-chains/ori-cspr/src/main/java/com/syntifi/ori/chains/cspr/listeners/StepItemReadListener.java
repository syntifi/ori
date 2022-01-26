package com.syntifi.ori.chains.cspr.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;

public class StepItemReadListener implements ItemReadListener<String> {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

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