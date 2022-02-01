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
        for (TransactionObject t : item.getChainTransfers()) {
            TransactionDTO transfer = new TransactionDTO();
            transfer.setHash(t.getHash());
            transfer.setBlockHash(t.getBlockHash());
            transfer.setFromHash(t.getFrom());
            transfer.setToHash(t.getTo());
            transfer.setAmount(t.getValue().doubleValue());
            // TODO: check this
            transfer.setTimeStamp(new Date(item.getChainBlock().getResult().getTimestamp().longValue()));
            transfers.add(transfer);
        }
        result.setTransfers(transfers);

        return result;
    }
}
