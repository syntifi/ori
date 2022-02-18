package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.model.transfer.TransferData;
import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.reader.AbstractChainReader;
import com.syntifi.ori.chains.cspr.model.CsprChainData;
import com.syntifi.ori.client.OriClient;

import org.springframework.stereotype.Component;

/**
 * {@link AbstractChainReader} for Casper block chain
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Component
public class CsprChainReader
        extends AbstractChainReader<CasperService, CsprChainData> {

    public CsprChainReader(CasperService chainService, OriClient oriClient,
            OriChainConfigProperties oriChainConfigProperties) {
        super(chainService, oriClient, oriChainConfigProperties);
    }

    @Override
    public CsprChainData read() throws IOException, InterruptedException {
        JsonBlockData blockData = getChainService().getBlock(new HeightBlockIdentifier(getBlockHeight()));

        // Stop reading if no block data for given height
        if (blockData == null) {
            return null;
        }

        TransferData transferData = getChainService().getBlockTransfers(new HeightBlockIdentifier(getBlockHeight()));

        nextItem();

        return new CsprChainData(blockData.getBlock(), transferData.getTransfers());
    }
}
