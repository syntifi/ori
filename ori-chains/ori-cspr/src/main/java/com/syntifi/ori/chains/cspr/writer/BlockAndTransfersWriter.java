package com.syntifi.ori.chains.cspr.writer;

import java.util.List;

import com.syntifi.ori.chains.cspr.model.OriBlockAndTransfers;
import com.syntifi.ori.client.OriRestClient;
import com.syntifi.ori.model.OriAccountPost;

import org.springframework.batch.item.ItemWriter;

public class BlockAndTransfersWriter implements ItemWriter<OriBlockAndTransfers> {

    private String tokenSymbol;
    private OriRestClient oriRestClient;

    public BlockAndTransfersWriter(OriRestClient oriClient, String token) {
        oriRestClient = oriClient;
        tokenSymbol = token;
    }

    @Override
    public void write(List<? extends OriBlockAndTransfers> blockAndTransfersResults) {
        // Write Block
        for (OriBlockAndTransfers oriBlockAndTransfers : blockAndTransfersResults) {
            oriRestClient.postBlock(tokenSymbol,
                    oriBlockAndTransfers.getParentBlock(),
                    oriBlockAndTransfers.getBlock());
            if (oriBlockAndTransfers.getTransfers() != null) {
                for (int i = 0; i < oriBlockAndTransfers.getTransfers().size(); i++) {
                    oriRestClient.postAccount(tokenSymbol,
                            new OriAccountPost("", oriBlockAndTransfers.getFrom().get(i), ""));
                    oriRestClient.postAccount(tokenSymbol,
                            new OriAccountPost("", oriBlockAndTransfers.getTo().get(i), ""));
                    oriRestClient.postTransfer(tokenSymbol,
                            oriBlockAndTransfers.getBlock().getHash(),
                            oriBlockAndTransfers.getFrom().get(i),
                            oriBlockAndTransfers.getTo().get(i),
                            oriBlockAndTransfers.getTransfers().get(i));
                }
            }
        }
    }
}
