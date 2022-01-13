package com.syntifi.ori.chains.cspr.processor;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.transfer.Transfer;
import com.syntifi.ori.chains.cspr.model.CsprBlockAndTransfers;
import com.syntifi.ori.chains.cspr.model.OriBlockAndTransfers;
import com.syntifi.ori.model.OriBlockPost;
import com.syntifi.ori.model.OriTransferPost;

import org.springframework.batch.item.ItemProcessor;

public class BlockAndTransfersProcessor implements ItemProcessor<CsprBlockAndTransfers, OriBlockAndTransfers> {

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public OriBlockAndTransfers process(CsprBlockAndTransfers blockAndTransfers) throws Exception {
        final OriBlockAndTransfers result = new OriBlockAndTransfers();

        // Block processor
        JsonBlock casperBlock = blockAndTransfers.getBlock();
        OriBlockPost block = new OriBlockPost();
        block.setEra(casperBlock.getHeader().getEraId());
        block.setHash(casperBlock.getHash());
        block.setHeight(casperBlock.getHeader().getHeight());
        block.setRoot(casperBlock.getHeader().getStateRootHash());
        block.setValidator(
                new BigInteger(casperBlock.getBody().getProposer().getKey()).toString(16));
        block.setTimeStamp(
                dateFormatter.format(casperBlock.getHeader().getTimeStamp()) + "+0000");

        result.setBlock(block);
        result.setParentBlock(blockAndTransfers.getBlock().getHeader().getParentHash());

        // Transfer processor
       
        List<OriTransferPost> transfers = new LinkedList<>();
        List<String> from = new LinkedList<>();
        List<String> to = new LinkedList<>();
        for (Transfer t : blockAndTransfers.getTransfers()) {
            OriTransferPost transfer = new OriTransferPost();
            transfer.setTimeStamp(
                    dateFormatter.format(casperBlock.getHeader().getTimeStamp()) + "+0000");
            transfer.setAmount(t.getAmount().doubleValue());
            transfer.setHash(t.getDeployHash());
            transfers.add(transfer);
            from.add(t.getFrom());
            to.add(t.getTo());
        }

        return result;
    }

}
