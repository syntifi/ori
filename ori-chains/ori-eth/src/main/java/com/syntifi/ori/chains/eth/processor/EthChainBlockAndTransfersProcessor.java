package com.syntifi.ori.chains.eth.processor;

import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;
import com.syntifi.ori.chains.base.processor.AbstractChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.eth.model.EthChainBlockAndTransfers;
import com.syntifi.ori.model.OriBlockPost;
import com.syntifi.ori.model.OriTransferPost;

import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;

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

        // Transfer processor
        List<OriTransferPost> transfers = new LinkedList<>();
        List<String> from = new LinkedList<>();
        List<String> to = new LinkedList<>();
        for (TransactionObject t: item.getChainTransfers()) {
            OriTransferPost transfer = new OriTransferPost();
            transfer.setTimeStamp(getDateString(item.getChainBlock().getResult().getTimestamp()));
            transfer.setAmount(t.getValue().doubleValue());
            transfer.setHash(t.getHash());
            transfers.add(transfer);
            from.add(t.getFrom());
            to.add(t.getTo());
        }
        return out;
    }

}
