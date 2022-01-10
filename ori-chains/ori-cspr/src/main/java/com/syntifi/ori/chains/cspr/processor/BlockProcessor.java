package com.syntifi.ori.chains.cspr.processor;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

import com.google.gson.JsonObject;
import com.syntifi.casper.sdk.model.block.JsonBlock;

import org.springframework.batch.item.ItemProcessor;

public class BlockProcessor implements ItemProcessor<JsonBlock, JsonObject> {

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public JsonObject process(JsonBlock casperBlock) throws Exception {
        JsonObject block = new JsonObject();
        block.addProperty("era", casperBlock.getHeader().getEraId());
        block.addProperty("hash", casperBlock.getHash());
        block.addProperty("height", casperBlock.getHeader().getHeight());
        block.addProperty("parent", casperBlock.getHeader().getParentHash());
        block.addProperty("root", casperBlock.getHeader().getStateRootHash());
        block.addProperty("validator",
                new BigInteger(casperBlock.getBody().getProposer().getKey()).toString(16));
        block.addProperty("timeStamp",
                dateFormatter.format(casperBlock.getHeader().getTimeStamp()) + "+0000");
        return block;
    }

}
