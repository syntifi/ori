package com.syntifi.ori.chains.eth.processor;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;
import com.syntifi.ori.chains.base.processor.AbstractChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.eth.model.EthChainBlockAndTransfers;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;

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

        // Transfer processor
        List<TransactionDTO> transfers = new LinkedList<>();
        List<String> from = new LinkedList<>();
        List<String> to = new LinkedList<>();
        for (TransactionObject t : item.getChainTransfers()) {
            TransactionDTO transfer = new TransactionDTO();
            // TODO: check this
            transfer.setTimeStamp(new Date(item.getChainBlock().getResult().getTimestamp().longValue()));
            transfer.setAmount(t.getValue().doubleValue());
            transfer.setHash(t.getHash());
            transfers.add(transfer);
            from.add(t.getFrom());
            to.add(t.getTo());
        }

        return result;
    }
}
