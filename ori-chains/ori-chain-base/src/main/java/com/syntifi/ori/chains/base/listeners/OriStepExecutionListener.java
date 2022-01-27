package com.syntifi.ori.chains.base.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

public class OriStepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        logger.info("OriStepExecutionListener - beforeStep");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("OriStepExecutionListener - afterStep");

        return stepExecution.getExitStatus();
    }
}