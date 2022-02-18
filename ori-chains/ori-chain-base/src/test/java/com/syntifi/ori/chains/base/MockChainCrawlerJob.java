package com.syntifi.ori.chains.base;

import com.syntifi.ori.chains.base.model.MockChainData;
import com.syntifi.ori.chains.base.processor.MockChainProcessor;
import com.syntifi.ori.chains.base.reader.MockChainReader;
import com.syntifi.ori.chains.base.service.MockTestChainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class MockChainCrawlerJob extends AbstractChainCrawlerJob<MockTestChainService, MockChainData> {

    @Autowired
    private MockChainReader chainReader;

    @Autowired
    private MockChainProcessor chainProcessor;

    @Autowired
    private MockTestChainService chainService;
}
