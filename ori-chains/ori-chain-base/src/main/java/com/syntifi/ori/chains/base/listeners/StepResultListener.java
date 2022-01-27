package com.syntifi.ori.chains.base.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class StepResultListener implements StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("Called beforeStep().");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("Called afterStep().");
        return stepExecution.getExitStatus();
    }
}