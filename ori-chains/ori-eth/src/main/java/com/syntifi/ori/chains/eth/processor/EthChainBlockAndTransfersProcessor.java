package com.syntifi.ori.chains.eth.processor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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
        Instant timestamp = Instant.ofEpochSecond(item.getChainBlock().getResult().getTimestamp().longValue());
        block.setTimeStamp(OffsetDateTime.ofInstant(timestamp, ZoneId.of("UTC")));
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
            Instant timestampTransaction = Instant
                    .ofEpochSecond(item.getChainBlock().getResult().getTimestamp().longValue());
            transfer.setTimeStamp(OffsetDateTime.ofInstant(timestampTransaction, ZoneId.of("UTC")));
            transfers.add(transfer);
        }
        result.setTransfers(transfers);

        return result;
    }
}
