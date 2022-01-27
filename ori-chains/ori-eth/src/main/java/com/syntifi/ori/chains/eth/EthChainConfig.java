package com.syntifi.ori.chains.eth;

import java.io.IOException;

import com.syntifi.ori.chains.base.AbstractChainConfig;
import com.syntifi.ori.chains.eth.processor.EthChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.eth.reader.EthChainBlockAndTransfersReader;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import lombok.Getter;

@Configuration
@ComponentScan
@Getter
public class EthChainConfig extends AbstractChainConfig<Web3j> {

    @Value("${ori.host}")
    private String oriHost;

    @Value("${eth.node}")
    private String chainNode;

    @Value("${eth.port}")
    private int chainNodePort;

    @Value("${eth.token}")
    private String tokenSymbol;

    @Value("${eth.token.name}")
    private String tokenName;

    @Value("${eth.token.protocol}")
    private String tokenProtocol;

    @Value("${eth.block.zero.hash}")
    private String blockZeroHash;

    @Value("${eth.block.zero.height}")
    private long blockZeroHeight;

    @Value("${eth.batch.chunk.size}")
    private int chunkSize;

    @Bean(name = "ethService")
    @Override
    public Web3j service() throws IOException {
        return Web3j.build(new HttpService("http://" + chainNode + ":" + chainNodePort));
    }

    @Bean(name = "ethBlockAndTransferProcessor")
    public EthChainBlockAndTransfersProcessor blockAndTransferProcessor() {
        return new EthChainBlockAndTransfersProcessor();
    }

    @Bean(name = "ethBlockAndTransferReader")
    public EthChainBlockAndTransfersReader blockAndTransferReader(@Autowired Web3j ethService,
            @Autowired OriRestClient oriRestClient, @Autowired EthChainConfig chainConfig) {
        return new EthChainBlockAndTransfersReader(ethService, oriRestClient, chainConfig.getTokenSymbol());
    }
}