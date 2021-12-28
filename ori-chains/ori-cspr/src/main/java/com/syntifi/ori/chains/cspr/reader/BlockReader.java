package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;

import com.syntifi.casper.Casper;
import com.syntifi.casper.model.chain.get.block.CasperBlock;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.BlockRepository;

import org.eclipse.microprofile.config.ConfigProvider;
import org.springframework.batch.item.ItemReader;

public class BlockReader implements ItemReader<CasperBlock> {

    private long nextBlockHeight;
    private Casper casper;

    @Inject
    BlockRepository blockRepository;

    public BlockReader(Token token) {
        casper = new Casper(
                Arrays.asList(ConfigProvider.getConfig().getValue("casper.nodes", String.class).split(",")),
                ConfigProvider.getConfig().getValue("casper.port", int.class),
                ConfigProvider.getConfig().getValue("casper.timeout", int.class),
                ConfigProvider.getConfig().getValue("casper.threads", int.class));
        nextBlockHeight = blockRepository.getLastBlock(token).getHeight() + 1;
    }

    @Override
    public CasperBlock read() throws IOException, InterruptedException{
        return casper.getBlockByHeight(nextBlockHeight);
    }

}
