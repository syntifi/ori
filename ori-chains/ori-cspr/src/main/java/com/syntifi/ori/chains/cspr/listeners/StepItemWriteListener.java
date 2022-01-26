package com.syntifi.ori.chains.cspr.listeners;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;

public class StepItemWriteListener implements ItemWriteListener<Number> {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void beforeWrite(List<? extends Number> items) {
        logger.info("ItemWriteListener - beforeWrite");
    }

    @Override
    public void afterWrite(List<? extends Number> items) {
        logger.info("ItemWriteListener - afterWrite");
    }

    @Override
    public void onWriteError(Exception exception, List<? extends Number> items) {
        logger.info("ItemWriteListener - onWriteError");
    }
}