package com.syntifi.ori.chains.base.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class OriJobExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OriJobExecutionListener.class);

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        LOGGER.info("Before Job");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        LOGGER.info("After Job");
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            // job success
            LOGGER.info("Success");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            // job failure
            LOGGER.info("Failure");
        }
    }
}