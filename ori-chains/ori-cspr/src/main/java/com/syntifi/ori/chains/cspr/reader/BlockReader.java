package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.service.CasperService;

import org.springframework.batch.item.ItemReader;

public class BlockReader implements ItemReader<JsonBlock> {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private long nextBlockHeight;
    private CasperService casper;

    public BlockReader(String token, String restHttp, String csprNode, int csprPort) {
        try {
            casper = CasperService.usingPeer(csprNode, csprPort);
        } catch (MalformedURLException e) {
            logger.severe("*********** Malformed URL Exception thrown while executing BlockReader ***********");
        }
        //TODO: QUERY THE LATEST BLOCK IN THE DB?
        /*try {
            WebClient webclient = WebClient.create(restHttp);
            nextBlockHeight = webclient
            .get()
            .uri("/api/v2/block/" + token)
            .retrieve()
            .toEntity(JsonObject.class)
        }*/
        nextBlockHeight = 0L;
    }

    @Override
    public JsonBlock read() throws IOException, InterruptedException {
        JsonBlockData blockData = casper.getBlock(new HeightBlockIdentifier(nextBlockHeight));
        return blockData.getBlock();
    }

}
