package com.syntifi.ori.chains.base.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

/**
 * Chain JobExecutionListener for listening to batch job execution events
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class OriJobExecutionListener {

    protected static final Log logger = LogFactory.getLog(OriJobExecutionListener.class);

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Before Job");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        logger.info("After Job");
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            // job success
            logger.info("Success");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            // job failure
            logger.info("Failure");
        }
    }
}