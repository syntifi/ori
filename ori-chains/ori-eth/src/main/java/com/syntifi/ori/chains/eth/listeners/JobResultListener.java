package com.syntifi.ori.chains.eth.listeners;

import java.util.logging.Logger;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobResultListener implements JobExecutionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public void beforeJob(JobExecution jobExecution) {
        logger.info("Called beforeJob().");
    }

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