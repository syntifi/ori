package com.syntifi.ori.chains.cspr.writer;

import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonObject;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.batch.item.ItemWriter;

public class BlockWriter implements ItemWriter<JsonObject> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private String tokenSymbol;
    private OriRestClient oriRestClient;

    public BlockWriter(OriRestClient oriClient, String token) {
        oriRestClient = oriClient;
        tokenSymbol = token;
    }

    @Override
    public void write(List<? extends JsonObject> blocks) {
        for (JsonObject block : blocks) {
            String parentBlock = block.get("parent").getAsString();
            block.remove("parent");
            JsonObject response = oriRestClient.postBlock(tokenSymbol, parentBlock, block);
            logger.info(response.toString());
        }
    }
}
