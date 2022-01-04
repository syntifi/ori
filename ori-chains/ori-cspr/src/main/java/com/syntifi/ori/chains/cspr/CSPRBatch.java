package com.syntifi.ori.chains.cspr;

import javax.inject.Inject;

import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.ori.chains.cspr.listeners.CustomChunkListener;
import com.syntifi.ori.chains.cspr.listeners.JobResultListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemProcessListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemReadListener;
import com.syntifi.ori.chains.cspr.listeners.StepItemWriteListener;
import com.syntifi.ori.chains.cspr.listeners.StepResultListener;
import com.syntifi.ori.chains.cspr.processor.BlockProcessor;
import com.syntifi.ori.chains.cspr.reader.BlockReader;
import com.syntifi.ori.chains.cspr.writer.BlockWriter;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.TokenRepository;

import org.eclipse.microprofile.config.ConfigProvider;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class CSPRBatch {

    @Inject
    private TokenRepository tokenRepository;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    public static void main(String[] args) {
		System.exit(SpringApplication.exit(
			SpringApplication.run(CSPRBatch.class, args)));
	}

    @Bean
    public Step step1ReadBlock(Token token, String restHttp) {
        return stepBuilderFactory.get("step1ReadBlock")
                .<JsonBlock, Block>chunk(1)
                .reader(new BlockReader(token))
                .processor(new BlockProcessor(token))
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
        Token token =  tokenRepository.findBySymbol("CSPR");
        String restHttp = ConfigProvider.getConfig().getValue("ori.rest.api", String.class);
        return jobBuilderFactory.get("getBlockTransactionAndWrite")
                .incrementer(new RunIdIncrementer())
                .listener(new JobResultListener())
                .start(step1ReadBlock(token, restHttp))
                // .next(stepTwo())
                .build();
    }

}