package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.model.transfer.TransferData;
import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.reader.AbstractChainBlockAndTransfersReader;
import com.syntifi.ori.chains.cspr.CsprChainConfig;
import com.syntifi.ori.chains.cspr.model.CsprChainBlockAndTransfers;
import com.syntifi.ori.client.OriRestClient;

public class CsprChainBlockAndTransfersReader
        extends AbstractChainBlockAndTransfersReader<CasperService, CsprChainBlockAndTransfers> {

    public CsprChainBlockAndTransfersReader(CasperService chainService, OriRestClient oriRestClient,
            CsprChainConfig chainConfig) {
        super(chainService, oriRestClient, chainConfig);
    }

    @Override
    public CsprChainBlockAndTransfers read() throws IOException, InterruptedException {
        if (getBlockHeight() == null)
            return null;
        JsonBlockData blockData = getChainService().getBlock(new HeightBlockIdentifier(getBlockHeight()));
        TransferData transferData = getChainService().getBlockTransfers(new HeightBlockIdentifier(getBlockHeight()));
        nextItem();
        return new CsprChainBlockAndTransfers(blockData.getBlock(), transferData.getTransfers());
    }
}
