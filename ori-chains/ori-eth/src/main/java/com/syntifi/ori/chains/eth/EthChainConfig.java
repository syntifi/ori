package com.syntifi.ori.chains.eth;

import java.io.IOException;

import com.syntifi.ori.chains.base.AbstractChainConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import lombok.Getter;

@Getter
@Component
public class EthChainConfig implements AbstractChainConfig<Web3j> {

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

    @Bean
    @Override
    public Web3j service() throws IOException {
        return Web3j.build(new HttpService("http://" + chainNode + ":" + chainNodePort));
    }
}