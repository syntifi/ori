package com.syntifi.ori.chains.eth;

import com.syntifi.ori.chains.base.AbstractChainCrawlerJob;
import com.syntifi.ori.chains.eth.model.EthChainData;
import com.syntifi.ori.chains.eth.processor.EthChainProcessor;
import com.syntifi.ori.chains.eth.reader.EthChainReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import lombok.Getter;

/**
 * Implementation class of {@link AbstractChainCrawlerJob} for Ethereum
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Getter
@Component
public class EthChainCrawlerJob extends AbstractChainCrawlerJob<Web3j, EthChainData> {

    @Autowired
    private EthChainReader chainReader;

    @Autowired
    private EthChainProcessor chainProcessor;

    @Autowired
    private Web3j chainService;
}
