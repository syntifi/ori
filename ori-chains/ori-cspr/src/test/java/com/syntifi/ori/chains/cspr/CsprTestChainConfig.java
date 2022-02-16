package com.syntifi.ori.chains.cspr;

import java.net.MalformedURLException;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.OriChainConfig;
import com.syntifi.ori.chains.base.writer.OriWriter;
import com.syntifi.ori.chains.cspr.processor.CsprChainProcessor;
import com.syntifi.ori.chains.cspr.reader.CsprChainReader;
import com.syntifi.ori.client.MockOriRestClient;
import com.syntifi.ori.client.OriClient;

import org.mockito.Mockito;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableBatchProcessing
@EnableAutoConfiguration
@Configuration
@Import(value = { OriWriter.class, CsprChainReader.class, CsprChainProcessor.class })
public class CsprTestChainConfig extends OriChainConfig {

    @Bean
    protected OriClient getOriClient() {
        return new MockOriRestClient();
    }

    @Bean
    protected CasperService getServiceInstance() throws MalformedURLException {
        return Mockito.mock(CasperService.class);
    }
}
