package com.syntifi.ori.chains.cspr;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.AbstractChainCrawlerJob;
import com.syntifi.ori.chains.cspr.model.CsprChainData;
import com.syntifi.ori.chains.cspr.processor.CsprChainProcessor;
import com.syntifi.ori.chains.cspr.reader.CsprChainReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class CsprChainCrawlerJob extends AbstractChainCrawlerJob<CasperService, CsprChainData> {
    
    @Autowired
    private CsprChainReader chainReader;

    @Autowired
    private CsprChainProcessor chainProcessor;

    @Autowired
    private CasperService chainService;
}
