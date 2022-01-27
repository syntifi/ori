package com.syntifi.ori.chains.eth.processor;

import java.math.BigInteger;
import java.util.Date;

import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;
import com.syntifi.ori.chains.base.processor.AbstractChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.eth.model.EthChainBlockAndTransfers;
import com.syntifi.ori.model.OriBlockPost;

public class EthChainBlockAndTransfersProcessor
        extends AbstractChainBlockAndTransfersProcessor<EthChainBlockAndTransfers> {

    private String getDateString(BigInteger val) {
        Date date = new Date(val.longValue() * 1000);
        return dateFormatter.format(date) + "+0000";
    }

    @Override
    public OriBlockAndTransfers process(EthChainBlockAndTransfers item) throws Exception {

        // Block processor
        OriBlockAndTransfers out = new OriBlockAndTransfers();
        OriBlockPost block = new OriBlockPost();
        block.setEra(0L);
        block.setHash(item.getChainBlock().getResult().getHash());
        block.setHeight(item.getChainBlock().getResult().getNumber().longValue());
        block.setRoot(item.getChainBlock().getResult().getStateRoot());
        block.setValidator(item.getChainBlock().getResult().getMiner());
        block.setTimeStamp(getDateString(item.getChainBlock().getResult().getTimestamp()));
        out.setBlock(block);
        out.setParentBlock(item.getChainBlock().getResult().getParentHash());

        return out;
    }

}
