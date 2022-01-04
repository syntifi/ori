package com.syntifi.ori.chains.cspr.reader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import com.syntifi.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.syntifi.casper.sdk.model.block.JsonBlockData;
import com.syntifi.casper.sdk.service.CasperService;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;

public class BlockReader implements ItemReader<JsonBlockData> {

    private final String token;
    private final String restHttp;

    @Value( "${cspr.node}" )
    private String csprNode;

    @Value( "${cspr.port}" )
    private int csprPort;

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private long nextBlockHeight;
    private CasperService casper;


    public BlockReader(String token, String restHttp) {
        this.token = token;
        this.restHttp = restHttp;
        try {
            casper = CasperService.usingPeer(csprNode, csprPort);
        } catch (MalformedURLException e) {
            logger.severe("*********** Malformed URL Exception thrown while executing BlockReader ***********");
        }
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
    public JsonBlockData read() throws IOException, InterruptedException {
        return casper.getBlock(new HeightBlockIdentifier(nextBlockHeight));
    }

}
