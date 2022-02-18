package com.syntifi.ori.chains.cspr.processor;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.transfer.Transfer;
import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.chains.base.processor.AbstractChainProcessor;
import com.syntifi.ori.chains.cspr.model.CsprChainData;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.stereotype.Component;

/**
 * {@link AbstractChainProcessor} for Casper block chain
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Component
public class CsprChainProcessor extends AbstractChainProcessor<CsprChainData> {

    protected CsprChainProcessor(OriChainConfigProperties oriChainConfigProperties) {
        super(oriChainConfigProperties);
    }

    @Override
    public OriData process(CsprChainData item) throws Exception {
        final OriData result = new OriData();

        // Block processor
        JsonBlock casperBlock = item.getChainBlock();
        BlockDTO block = new BlockDTO();
        block.setTokenSymbol(oriChainConfigProperties.getChain().getTokenSymbol());
        block.setParent(item.getChainBlock().getHeader().getParentHash());
        block.setEra(casperBlock.getHeader().getEraId());
        block.setHash(casperBlock.getHash());
        block.setHeight(casperBlock.getHeader().getHeight());
        block.setRoot(casperBlock.getHeader().getStateRootHash());
        block.setValidator(new BigInteger(casperBlock.getBody().getProposer().getKey()).toString(16));
        block.setTimeStamp(
                OffsetDateTime.ofInstant(casperBlock.getHeader().getTimeStamp().toInstant(), ZoneId.of("UTC")));
        block.check();
        result.setBlock(block);

        // Transfer processor
        List<TransactionDTO> transfers = new LinkedList<>();
        result.setTransfers(transfers);
        for (Transfer chainTransfer : item.getChainTransfers()) {
            TransactionDTO oriTransfer = new TransactionDTO();
            oriTransfer.setTokenSymbol(oriChainConfigProperties.getChain().getTokenSymbol());
            oriTransfer.setBlockHash(block.getHash());
            oriTransfer.setFromHash(chainTransfer.getFrom());
            oriTransfer.setToHash(chainTransfer.getTo());
            oriTransfer.setTimeStamp(
                    OffsetDateTime.ofInstant(casperBlock.getHeader().getTimeStamp().toInstant(), ZoneId.of("UTC")));
            oriTransfer.setAmount(chainTransfer.getAmount().doubleValue());
            oriTransfer.setHash(chainTransfer.getDeployHash());
            oriTransfer.check();
            transfers.add(oriTransfer);
        }

        return result;
    }
}
