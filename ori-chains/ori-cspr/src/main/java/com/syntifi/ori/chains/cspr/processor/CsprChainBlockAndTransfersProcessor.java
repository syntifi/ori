package com.syntifi.ori.chains.cspr.processor;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.transfer.Transfer;
import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;
import com.syntifi.ori.chains.base.processor.AbstractChainBlockAndTransfersProcessor;
import com.syntifi.ori.chains.cspr.model.CsprChainBlockAndTransfers;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

public class CsprChainBlockAndTransfersProcessor
        extends AbstractChainBlockAndTransfersProcessor<CsprChainBlockAndTransfers> {

    @Override
    public OriBlockAndTransfers process(CsprChainBlockAndTransfers item) throws Exception {
        final OriBlockAndTransfers result = new OriBlockAndTransfers();

        // Block processor
        JsonBlock casperBlock = item.getChainBlock();
        BlockDTO block = new BlockDTO();
        block.setParent(item.getChainBlock().getHeader().getParentHash());
        block.setEra(casperBlock.getHeader().getEraId());
        block.setHash(casperBlock.getHash());
        block.setHeight(casperBlock.getHeader().getHeight());
        block.setRoot(casperBlock.getHeader().getStateRootHash());
        block.setValidator(new BigInteger(casperBlock.getBody().getProposer().getKey()).toString(16));
        block.setTimeStamp(casperBlock.getHeader().getTimeStamp());

        result.setBlock(block);

        // Transfer processor
        List<TransactionDTO> transfers = new LinkedList<>();
        result.setTransfers(transfers);
        for (Transfer chainTransfer : item.getChainTransfers()) {
            TransactionDTO oriTransfer = new TransactionDTO();
            oriTransfer.setBlockHash(block.getHash());
            oriTransfer.setFromHash(chainTransfer.getFrom());
            oriTransfer.setToHash(chainTransfer.getTo());
            oriTransfer.setTimeStamp(casperBlock.getHeader().getTimeStamp());
            oriTransfer.setAmount(chainTransfer.getAmount().doubleValue());
            oriTransfer.setHash(chainTransfer.getDeployHash());
            transfers.add(oriTransfer);
        }

        return result;
    }
}
