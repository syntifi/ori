package com.syntifi.ori.chains.cspr.processor;

import com.google.gson.JsonObject;
import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.block.JsonBlockData;

import org.springframework.batch.item.ItemProcessor;

public class BlockProcessor implements ItemProcessor<JsonBlockData, JsonObject> {

    @Override
    public JsonObject process(JsonBlockData casperBlockData) throws Exception {
        JsonBlock casperBlock = casperBlockData.getBlock();
        JsonObject block = new JsonObject();
        block.addProperty("era", casperBlock.getHeader().getEraId());
        block.addProperty("hash", casperBlock.getHash());
        block.addProperty("height", casperBlock.getHeader().getHeight());
        block.addProperty("parent", casperBlock.getHeader().getParentHash());
        block.addProperty("root", casperBlock.getHeader().getStateRootHash());
        block.addProperty("timeStamp", casperBlock.getHeader().getTimeStamp().toString());
        return block;
    }

}
