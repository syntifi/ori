package com.syntifi.ori.chains.cspr.listeners;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobResultListener implements JobExecutionListener {

	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Called beforeJob().");
	}

	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			// job success
			System.out.println("Success");
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			// job failure
			System.out.println("Failure");
		}
	}
}