package com.syntifi.ori.chains.eth.writer;

import java.util.List;

import com.syntifi.ori.chains.eth.model.OriBlock;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.batch.item.ItemWriter;

public class BlockAndTransfersWriter implements ItemWriter<OriBlock> {

    private String tokenSymbol;
    private OriRestClient oriRestClient;

    public BlockAndTransfersWriter(OriRestClient oriClient, String token) {
        oriRestClient = oriClient;
        tokenSymbol = token;
    }

    @Override
    public void write(List<? extends OriBlock> blockAndTransfersResults) {
        // Write Block
        for (OriBlock oriBlock: blockAndTransfersResults) {
            oriRestClient.postBlock(tokenSymbol,
                    oriBlock.getParentBlock(),
                    oriBlock.getBlock());
        }
    }
}
