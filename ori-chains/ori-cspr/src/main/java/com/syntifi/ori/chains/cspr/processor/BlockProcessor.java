package com.syntifi.ori.chains.cspr.processor;

import javax.inject.Inject;

import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.BlockRepository;

import org.springframework.batch.item.ItemProcessor;

public class BlockProcessor implements ItemProcessor<JsonBlock, Block> {

    private final Token token;

    @Inject
    BlockRepository blockRepository;

    public BlockProcessor(Token token) {
        this.token = token;

    }

    @Override
    public Block process(JsonBlock casperBlock) throws Exception {
        final Block block = new Block();
        block.setEra(casperBlock.getHeader().getEraId());
        block.setHash(casperBlock.getHash());
        block.setHeight(casperBlock.getHeader().getHeight());
        block.setParent(blockRepository.findByHash(casperBlock.getHeader().getParentHash()));
        block.setRoot(casperBlock.getHeader().getStateRootHash());
        block.setTimeStamp(casperBlock.getHeader().getTimeStamp());
        block.setToken(token);
        return block;
    }

}
