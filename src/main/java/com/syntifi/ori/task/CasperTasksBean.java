package com.syntifi.ori.task;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.event.Observes;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class CasperTasksBean {

    @Inject
    Scheduler quartz; 

    private static final JobKey updateJobKey = new JobKey("updateJob", "Casper");
    private static final JobKey crawlJobKey = new JobKey("crawlJob", "Casper");

    //Schedule the job at startup, it will stay dorment, unless triggered
    void scheduleJobs(@Observes StartupEvent event) throws SchedulerException {
        if (quartz.isStarted()) {
            quartz.standby();
        } 

        JobDetail updateJob = JobBuilder.newJob(CasperUpdateJob.class)
                                .withIdentity(updateJobKey) 
                                .build();
        Trigger updateTrigger = TriggerBuilder.newTrigger()
                            .withIdentity("updateTrigger", "Casper")
                            .startNow()
                            .withSchedule(
                               SimpleScheduleBuilder.simpleSchedule()
                                  .withIntervalInSeconds(ConfigProvider
                                        .getConfig()
                                        .getValue("casper.transaction.update.freq", 
                                                    int.class))
                                  .repeatForever())
                            .build();
        quartz.scheduleJob(updateJob, updateTrigger); 
        quartz.pauseJob(updateJobKey);
        
        JobDetail crawlJob = JobBuilder.newJob(CasperCrawlJob.class)
                                .withIdentity(crawlJobKey) 
                                .build();
        Trigger crawlTrigger = TriggerBuilder.newTrigger()
                            .withIdentity("crawlTrigger", "Casper")
                            .startNow()
                            .withSchedule(
                               SimpleScheduleBuilder.simpleSchedule()
                                  .withIntervalInSeconds(ConfigProvider
                                        .getConfig()
                                        .getValue("casper.transaction.crawl.freq", 
                                                    int.class))
                                  .repeatForever())
                            .build();
        quartz.scheduleJob(crawlJob, crawlTrigger); 
        quartz.pauseJob(crawlJobKey);
        //JobDetail crawlJob = JobBuilder.newJob(CasperCrawlJob.class)
        //                        .withIdentity(crawlJobKey) 
        //                        .storeDurably()
        //                        .build();

        //quartz.addJob(crawlJob, true);
        //quartz.pauseJob(crawlJobKey);

        if (!quartz.isStarted()) {
            quartz.start();
        } 
    }

    public void pauseJob(JobKey jobKey) throws SchedulerException {
        quartz.pauseJob(jobKey);
    }

    public void triggerJob(JobKey jobKey) throws SchedulerException {
        quartz.triggerJob(jobKey);
    }

    public void stopJob(JobKey jobKey) throws SchedulerException {
        quartz.interrupt(jobKey);
    }

    public void resumeJob(JobKey jobKey) throws SchedulerException {
        quartz.resumeJob(jobKey);
    }

    public JobDetail getJobDetail(JobKey jobKey) throws SchedulerException {
        return quartz.getJobDetail(jobKey);
    }

}