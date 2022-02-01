package com.syntifi.ori.chains.base.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

public class OriStepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OriStepExecutionListener.class);

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        LOGGER.info("Before Step");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("After Step");

        return stepExecution.getExitStatus();
    }
}