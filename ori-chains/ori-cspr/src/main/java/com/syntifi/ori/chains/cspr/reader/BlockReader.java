package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.inject.Inject;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.BlockRepository;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;

public class BlockReader implements ItemReader<JsonBlock> {

    private long nextBlockHeight;
    private CasperService casperService;

    @Value( "${casper.node}" )
    private String csprNode;

    @Value( "${casper.port}" )
    private int csprPort;

    @Inject
    BlockRepository blockRepository;

    public BlockReader(Token token) {
        try {
            casperService = CasperService.usingPeer(csprNode, csprPort);
        } catch (MalformedURLException e) {
            // TODO Throw exception, maybe remove this throw in favor of a RuntimeException in casper-sdk
            e.printStackTrace();
        }
        nextBlockHeight = blockRepository.getLastBlock(token).getHeight() + 1;
    }

    @Override
    public JsonBlock read() throws IOException, InterruptedException {
        return casperService.getBlock(new HeightBlockIdentifier(nextBlockHeight)).getBlock();
    }

}
