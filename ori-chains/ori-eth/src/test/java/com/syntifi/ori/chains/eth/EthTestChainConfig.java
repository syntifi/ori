package com.syntifi.ori.chains.eth;

import static org.mockito.Mockito.withSettings;

import com.syntifi.ori.chains.base.OriChainConfig;
import com.syntifi.ori.chains.base.writer.OriWriter;
import com.syntifi.ori.chains.eth.processor.EthChainProcessor;
import com.syntifi.ori.chains.eth.reader.EthChainReader;
import com.syntifi.ori.client.MockOriRestClient;
import com.syntifi.ori.client.OriClient;

import org.mockito.Mockito;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.http.HttpService;

@EnableBatchProcessing
@Configuration
@Import(value = { OriWriter.class, EthChainReader.class, EthChainProcessor.class })
public class EthTestChainConfig extends OriChainConfig {

    @Bean
    protected OriClient getOriClient() {
        return new MockOriRestClient();
    }

    @Bean
    protected Web3j getServiceInstance() {
        return Mockito.mock(JsonRpc2_0Web3j.class,
                withSettings()
                        .useConstructor(new HttpService("http://" + getOriChainConfigProperties().getChainNode() + ":"
                                + getOriChainConfigProperties().getChainNodePort())));
    }
}
