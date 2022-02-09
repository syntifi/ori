package com.syntifi.ori.chains.base.reader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.chains.base.MockChainConfig;
import com.syntifi.ori.chains.base.client.MockChainService;
import com.syntifi.ori.chains.base.model.MockChainBlock;
import com.syntifi.ori.chains.base.model.MockChainData;
import com.syntifi.ori.chains.base.model.MockChainTransfer;
import com.syntifi.ori.client.OriClient;

import org.springframework.stereotype.Component;

@Component
public class MockChainReader
        extends AbstractChainReader<MockChainService, MockChainData> {

    public MockChainReader(MockChainService chainService, OriClient oriClient,
            MockChainConfig chainConfig) {
        super(chainService, oriClient, chainConfig);
    }

    @Override
    public MockChainData read() throws IOException, InterruptedException {
        MockChainBlock chainBlock = getChainService().getMockBlock();
        if (chainBlock == null)
            return null;

        List<MockChainTransfer> chainTransfers = new LinkedList<>();

        chainTransfers.addAll(getChainService().getMockTransfers());

        return MockChainData.builder()
                .chainBlock(chainBlock)
                .chainTransfers(chainTransfers)
                .build();
    }
}
