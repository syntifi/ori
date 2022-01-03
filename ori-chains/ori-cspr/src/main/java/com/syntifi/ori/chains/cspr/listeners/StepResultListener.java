package com.syntifi.ori.chains.cspr.listeners;

import java.util.logging.Logger;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class StepResultListener implements StepExecutionListener {

    Logger logger = Logger.getLogger(this.getClass().getName());

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