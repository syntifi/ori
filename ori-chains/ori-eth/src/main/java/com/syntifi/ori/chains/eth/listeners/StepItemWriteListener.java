package com.syntifi.ori.chains.eth.listeners;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.batch.core.ItemWriteListener;

public class StepItemWriteListener implements ItemWriteListener<Number> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

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