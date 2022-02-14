package com.syntifi.ori.chains.base.reader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.client.MockTestChainService;
import com.syntifi.ori.chains.base.model.MockChainBlock;
import com.syntifi.ori.chains.base.model.MockChainData;
import com.syntifi.ori.chains.base.model.MockChainTransfer;
import com.syntifi.ori.client.OriClient;

import org.springframework.stereotype.Component;

@Component
public class MockChainReader
        extends AbstractChainReader<MockTestChainService, MockChainData> {

    public MockChainReader(MockTestChainService chainService, OriClient oriClient,
            OriChainConfigProperties oriChainConfigProperties) {
        super(chainService, oriClient, oriChainConfigProperties);
    }

    @Override
    public MockChainData read() throws IOException, InterruptedException {
        MockChainBlock chainBlock = getChainService().getBlock();
        if (chainBlock == null)
            return null;

        List<MockChainTransfer> chainTransfers = new LinkedList<>();

        chainTransfers.addAll(getChainService().getTransfers(chainBlock.getHash()));

        return MockChainData.builder()
                .chainBlock(chainBlock)
                .chainTransfers(chainTransfers)
                .build();
    }
}
