package com.syntifi.ori.chains.base.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class OriJobExecutionListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Called beforeJob().");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            // job success
            logger.info("Success");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            // job failure
            logger.info("Failure");
        }
    }
}