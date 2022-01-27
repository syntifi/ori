package com.syntifi.ori.chains.eth.processor;

import java.util.Date;

import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;
import com.syntifi.ori.chains.base.processor.AbstractChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.eth.model.EthChainBlockAndTransfers;
import com.syntifi.ori.dto.BlockDTO;

public class EthChainBlockAndTransfersProcessor
        extends AbstractChainBlockAndTransfersProcessor<EthChainBlockAndTransfers> {

    @Override
    public OriBlockAndTransfers process(EthChainBlockAndTransfers item) throws Exception {
        final OriBlockAndTransfers result = new OriBlockAndTransfers();

        // Block processor
        BlockDTO block = new BlockDTO();
        block.setHash(item.getChainBlock().getResult().getHash());
        block.setHeight(item.getChainBlock().getResult().getNumber().longValue());
        block.setParent(item.getChainBlock().getResult().getParentHash());
        block.setRoot(item.getChainBlock().getResult().getStateRoot());
        block.setEra(0L);
        block.setValidator(item.getChainBlock().getResult().getMiner());
        // TODO: check this
        block.setTimeStamp(new Date(item.getChainBlock().getResult().getTimestamp().longValue()));
        result.setBlock(block);

        return result;
    }
}
