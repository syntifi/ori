package com.syntifi.ori.graphql;

import org.quartz.JobKey;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Description;

import javax.inject.Inject;

import com.syntifi.ori.task.CasperTasksBean;

@GraphQLApi
public class TaskGraphQLAPI {
    @Inject 
    CasperTasksBean casperTasksBean;

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
