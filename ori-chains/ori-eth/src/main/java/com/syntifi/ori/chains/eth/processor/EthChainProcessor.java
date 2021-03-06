package com.syntifi.ori.chains.eth.processor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.chains.base.processor.AbstractChainProcessor;
import com.syntifi.ori.chains.eth.model.EthChainData;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;

/**
 * {@link AbstractChainProcessor} for Ethereum block chain
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Component
public class EthChainProcessor extends AbstractChainProcessor<EthChainData> {

    protected EthChainProcessor(OriChainConfigProperties oriChainConfigProperties) {
        super(oriChainConfigProperties);
    }

    @Override
    public OriData process(EthChainData item) throws Exception {
        final OriData result = new OriData();

        // Block processor
        BlockDTO block = new BlockDTO();
        block.setTokenSymbol(oriChainConfigProperties.getChain().getTokenSymbol());
        block.setHash(item.getChainBlock().getResult().getHash());
        block.setHeight(item.getChainBlock().getResult().getNumber().longValue());
        block.setParent(item.getChainBlock().getResult().getParentHash());
        block.setRoot(item.getChainBlock().getResult().getStateRoot());
        block.setEra(0L);
        block.setValidator(item.getChainBlock().getResult().getMiner());
        Instant timestamp = Instant.ofEpochSecond(item.getChainBlock().getResult().getTimestamp().longValue());
        block.setTimeStamp(OffsetDateTime.ofInstant(timestamp, ZoneId.of("UTC")));
        block.check();
        result.setBlock(block);

        // Transfer processor
        List<TransactionDTO> transfers = new LinkedList<>();
        for (TransactionObject t : item.getChainTransfers()) {
            TransactionDTO transfer = new TransactionDTO();
            transfer.setTokenSymbol(oriChainConfigProperties.getChain().getTokenSymbol());
            transfer.setHash(t.getHash());
            transfer.setBlockHash(t.getBlockHash());
            transfer.setFromHash(t.getFrom());
            transfer.setToHash(t.getTo());
            transfer.setAmount(t.getValue().doubleValue());
            Instant timestampTransaction = Instant
                    .ofEpochSecond(item.getChainBlock().getResult().getTimestamp().longValue());
            transfer.setTimeStamp(OffsetDateTime.ofInstant(timestampTransaction, ZoneId.of("UTC")));
            transfer.check();
            transfers.add(transfer);
        }
        result.setTransfers(transfers);

        return result;
    }
}
