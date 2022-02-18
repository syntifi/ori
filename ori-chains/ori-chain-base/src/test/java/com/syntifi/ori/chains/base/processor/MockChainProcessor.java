package com.syntifi.ori.chains.base.processor;

import java.util.LinkedList;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.model.MockChainData;
import com.syntifi.ori.chains.base.model.MockChainTransfer;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.stereotype.Component;

@Component
public class MockChainProcessor extends AbstractChainProcessor<MockChainData> {

    protected MockChainProcessor(OriChainConfigProperties oriChainConfigProperties) {
        super(oriChainConfigProperties);
    }

    @Override
    public OriData process(MockChainData item) throws Exception {
        final OriData result = new OriData();

        BlockDTO blockDTO = item.getChainBlock().toDTO();
        blockDTO.setTokenSymbol(oriChainConfigProperties.getChain().getTokenSymbol());
        blockDTO.check();

        result.setBlock(blockDTO);

        result.setTransfers(new LinkedList<>());
        for (MockChainTransfer transfer : item.getChainTransfers()) {
            TransactionDTO transactionDTO = transfer.toDTO();
            transactionDTO.setTokenSymbol(getOriChainConfigProperties().getChain().getTokenSymbol());
            transactionDTO.check();
            result.getTransfers().add(transactionDTO);
        }

        return result;
    }
}
