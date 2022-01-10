package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.service.CasperService;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

public class BlockReader implements ItemReader<JsonBlock> {

    @Autowired 
    private WebClient oriRestClient;

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Long blockHeight;
    private CasperService casper;

    public BlockReader(String csprNode, int csprPort) {
        try {
            casper = CasperService.usingPeer(csprNode, csprPort);
            initialize();
        } catch (MalformedURLException e) {
            logger.severe("*********** Malformed URL Exception thrown while executing BlockReader ***********");
        }
    }

    private void initialize() {
/*         try {
            WebClient webclient = WebClient.create(restHttp);
            nextBlockHeight = webclient
            .get()
            .uri("/api/v2/block/" + token)
            .retrieve()
            .toEntity(JsonObject.class)
        } */
        blockHeight = 0L;
    }

    private boolean nextItem() {
        blockHeight += 1;
        return true;
    }

    // READ should return null if next item is not found
    @Override
    public JsonBlock read() throws IOException, InterruptedException {
        if (blockHeight == null)
            return null;
        JsonBlockData blockData = casper.getBlock(new HeightBlockIdentifier(blockHeight));
        nextItem();
        return blockData.getBlock();
    }

}
