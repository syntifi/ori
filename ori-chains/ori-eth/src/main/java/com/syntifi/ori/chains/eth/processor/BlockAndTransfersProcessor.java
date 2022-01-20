package com.syntifi.ori.chains.eth.processor;

import java.text.SimpleDateFormat;

import com.syntifi.ori.chains.eth.model.OriBlock;
import com.syntifi.ori.model.OriBlockPost;

import org.springframework.batch.item.ItemProcessor;
import org.web3j.protocol.core.methods.response.EthBlock;

public class BlockAndTransfersProcessor implements ItemProcessor<EthBlock, OriBlock> {

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public OriBlock process(EthBlock ethBlock) throws Exception {

        // Block processor
        OriBlock out = new OriBlock();
        OriBlockPost block = new OriBlockPost();
        block.setEra(0L);
        block.setHash(ethBlock.getResult().getHash());
        block.setHeight(ethBlock.getResult().getNumber().longValue());
        block.setRoot(ethBlock.getResult().getStateRoot());
        block.setValidator(ethBlock.getResult().getMiner());
        block.setTimeStamp(ethBlock.getResult().getTimestampRaw());
        out.setBlock(block);
        out.setParentBlock(ethBlock.getResult().getParentHash());

        return out;
    }

}
