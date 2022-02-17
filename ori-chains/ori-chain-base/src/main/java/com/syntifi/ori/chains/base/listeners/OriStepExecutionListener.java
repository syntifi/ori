package com.syntifi.ori.chains.base.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

/**
 * Chain StepExecutionListener for listening to batch step execution events
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class OriStepExecutionListener {

    protected static final Log logger = LogFactory.getLog(OriStepExecutionListener.class);

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        logger.info("Before Step");
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("After Step");

        return stepExecution.getExitStatus();
    }
}