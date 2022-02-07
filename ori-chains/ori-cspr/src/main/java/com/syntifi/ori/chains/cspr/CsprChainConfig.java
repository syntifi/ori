package com.syntifi.ori.chains.cspr;

import java.io.IOException;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.AbstractChainConfig;
import com.syntifi.ori.chains.cspr.processor.CsprChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.cspr.reader.CsprChainBlockAndTransfersReader;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@ComponentScan
@Getter
public class CsprChainConfig implements AbstractChainConfig<CasperService> {

    @Value("${ori.host}")
    private String oriHost;

    @Value("${cspr.node}")
    private String chainNode;

    @Value("${cspr.port}")
    private int chainNodePort;

    @Value("${cspr.token}")
    private String tokenSymbol;

    @Value("${cspr.token.name}")
    private String tokenName;

    @Value("${cspr.token.protocol}")
    private String tokenProtocol;

    @Value("${cspr.block.zero.hash}")
    private String blockZeroHash;

    @Value("${cspr.block.zero.height}")
    private long blockZeroHeight;

    @Value("${cspr.batch.chunk.size}")
    private int chunkSize;

    @Bean(name = "casperService")
    @Override
    public CasperService service() throws IOException {
        return CasperService.usingPeer(chainNode, chainNodePort);
    }

    @Bean(name = "casperBlockAndTransferProcessor")
    public CsprChainBlockAndTransfersProcessor blockAndTransferProcessor() {
        return new CsprChainBlockAndTransfersProcessor();
    }

    @Bean(name = "casperBlockAndTransferReader")
    public CsprChainBlockAndTransfersReader blockAndTransferReader(@Autowired CasperService casperService,
            @Autowired OriRestClient oriRestClient, @Autowired CsprChainConfig chainConfig) {
        return new CsprChainBlockAndTransfersReader(casperService, oriRestClient, chainConfig);
    }
}