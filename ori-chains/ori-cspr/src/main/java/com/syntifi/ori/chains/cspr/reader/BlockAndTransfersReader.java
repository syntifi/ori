package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.model.transfer.TransferData;
import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.cspr.model.CsprBlockAndTransfers;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.batch.item.ItemReader;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class BlockAndTransfersReader implements ItemReader<CsprBlockAndTransfers> {

    private Long blockHeight;
    private String tokenSymbol;
    private CasperService casper;
    private OriRestClient oriRestClient;

    public BlockAndTransfersReader(CasperService casperService,
            OriRestClient oriRestClient,
            String tokenSymbol) {
        this.casper = casperService;
        this.oriRestClient = oriRestClient;
        this.tokenSymbol = tokenSymbol;
        initialize();
    }

    private void initialize() {
        try {
            blockHeight = oriRestClient.getLastBlock(tokenSymbol).getHeight()+1;
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                blockHeight = 0L;
            }
        }
    }

    private boolean nextItem() {
        blockHeight += 1;
        return true;
    }

    // READ should return null if next item is not found
    @Override
    public CsprBlockAndTransfers read() throws IOException, InterruptedException {
        if (blockHeight == null)
            return null;
        JsonBlockData blockData = casper.getBlock(new HeightBlockIdentifier(blockHeight));
        TransferData transferData = casper.getBlockTransfers(new HeightBlockIdentifier(blockHeight));
        nextItem();
        return new CsprBlockAndTransfers(blockData.getBlock(), transferData.getTransfers());
    }

}
