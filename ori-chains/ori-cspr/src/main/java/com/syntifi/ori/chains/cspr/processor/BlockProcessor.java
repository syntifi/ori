package com.syntifi.ori.chains.cspr.processor;

import javax.inject.Inject;

import com.syntifi.casper.model.chain.get.block.CasperBlock;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.BlockRepository;

import org.springframework.batch.item.ItemProcessor;

public class BlockProcessor implements ItemProcessor<CasperBlock, Block> {

    private final Token token;

    @Inject
    BlockRepository blockRepository;

    public BlockProcessor(Token token) {
        this.token = token;

    }

    @Override
    public Block process(CasperBlock casperBlock) throws Exception {
        final Block block = new Block();
        block.setEra(casperBlock.header.eraId);
        block.setHash(casperBlock.hash);
        block.setHeight(casperBlock.header.height);
        block.setParent(blockRepository.findByHash(casperBlock.header.parentHash));
        block.setRoot(casperBlock.header.stateRootHash);
        block.setTimeStamp(casperBlock.header.timeStamp);
        block.setToken(token);
        return block;
    }

}
