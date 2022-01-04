package com.syntifi.ori.chains.cspr;

import com.google.gson.JsonObject;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.ori.chains.cspr.listeners.CustomChunkListener;
import com.syntifi.ori.chains.cspr.listeners.JobResultListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemProcessListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemReadListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemWriteListener;
import com.syntifi.ori.chains.cspr.listeners.StepResultListener;
import com.syntifi.ori.chains.cspr.processor.BlockProcessor;
import com.syntifi.ori.chains.cspr.reader.BlockReader;
import com.syntifi.ori.chains.cspr.writer.BlockWriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class CSPRBatch {

    private static final String token = "CSPR";

    @Value( "${ori.rest.api}" )
    private String restHttp;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1ReadBlock() {
        return stepBuilderFactory.get("step1ReadBlock")
                .<JsonBlockData, JsonObject>chunk(1)
                .reader(new BlockReader(token, restHttp))
                .processor(new BlockProcessor())
                .writer(new BlockWriter(token, restHttp))
                .listener(new StepResultListener())
                .listener(new CustomChunkListener())
                .listener(new StepItemReadListener())
                .listener(new StepItemProcessListener())
                .listener(new StepItemWriteListener())
                .build();
    }

    @Bean
    public Job getBlockTransactionAndWrite() {
        return jobBuilderFactory.get("getBlockTransactionAndWrite")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(step1ReadBlock())
                // .next(stepTwo())
                .build();
    }

}