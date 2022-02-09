package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.model.transfer.TransferData;
import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.reader.AbstractChainReader;
import com.syntifi.ori.chains.cspr.CsprChainConfig;
import com.syntifi.ori.chains.cspr.model.CsprChainData;
import com.syntifi.ori.client.OriClient;

import org.springframework.stereotype.Component;

@Component

public class CsprChainReader
        extends AbstractChainReader<CasperService, CsprChainData> {

    public CsprChainReader(CasperService chainService, OriClient oriClient,
            CsprChainConfig chainConfig) {
        super(chainService, oriClient, chainConfig);
    }

    @Override
    public CsprChainData read() throws IOException, InterruptedException {
        if (getBlockHeight() == null)
            return null;
        JsonBlockData blockData = getChainService().getBlock(new HeightBlockIdentifier(getBlockHeight()));
        TransferData transferData = getChainService().getBlockTransfers(new HeightBlockIdentifier(getBlockHeight()));
        nextItem();
        return new CsprChainData(blockData.getBlock(), transferData.getTransfers());
    }
}
