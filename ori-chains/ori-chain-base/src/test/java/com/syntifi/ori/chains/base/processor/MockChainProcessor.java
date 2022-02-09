package com.syntifi.ori.chains.base.processor;

import java.util.LinkedList;

import com.syntifi.ori.chains.base.model.MockChainData;
import com.syntifi.ori.chains.base.model.MockChainTransfer;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.stereotype.Component;

@Component
public class MockChainProcessor
        extends AbstractChainProcessor<MockChainData> {

    @Override
    public OriData process(MockChainData item) throws Exception {
        final OriData result = new OriData();

        result.setBlock(BlockDTO.builder()
                .hash(item.getChainBlock().getHash())
                .build());

        result.setTransfers(new LinkedList<>());
        for (MockChainTransfer transfer : item.getChainTransfers()) {
            result.getTransfers().add(TransactionDTO.builder()
                    .hash(transfer.getHash())
                    .build());
        }

        return result;
    }
}
