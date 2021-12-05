package com.syntifi.ori.graphql;

import org.quartz.JobKey;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Description;

import javax.inject.Inject;

import com.syntifi.ori.task.CasperTasksBean;

/**
 * GraphQL API queries to start, stop and pause the Casper Crawlers
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@GraphQLApi
public class TaskGraphQLAPI {
    @Inject 
    CasperTasksBean casperTasksBean;

    /**
     * Query to start the {@link CasperUpdateJob} Quatz job 
     * 
     * @return boolean
     */
    @Query
    @Description("Start Casper update job running repeatedly")
    public boolean getStartCasperUpdateJob() {
        var jobKey = new JobKey("updateJob", "Casper");
        try {
            casperTasksBean.resumeJob(jobKey);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Query to pause the {@link CasperUpdateJob} Quatz job 
     * 
     * @return boolean
     */
    @Query
    @Description("Pause Casper update job running repeatedly")
    public boolean getPauseCasperUpdateJob() {
        var jobKey = new JobKey("updateJob", "Casper");
        try {
            casperTasksBean.pauseJob(jobKey);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Query to start the {@link CasperCrawlJob} Quatz job 
     * 
     * @return boolean
     */
    @Query
    @Description("Start Casper batch job running in smaller parts")
    public boolean getStartCasperCrawlJob() {
        var jobKey = new JobKey("crawlJob", "Casper");
        try {
            casperTasksBean.resumeJob(jobKey);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Query to pause the {@link CasperCrawlJob} Quatz job 
     * 
     * @return boolean
     */
    @Query
    @Description("Pause Casper batch job")
    public boolean getStopCasperCrawlJob() {
        var jobKey = new JobKey("crawlJob", "Casper");
        try {
            casperTasksBean.pauseJob(jobKey);
        } catch (Exception e) {
            return false;
        }
        return true;
    }   


}
