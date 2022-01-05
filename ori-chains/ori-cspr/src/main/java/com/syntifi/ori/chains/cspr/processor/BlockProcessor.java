package com.syntifi.ori.chains.cspr.processor;

import com.google.gson.JsonObject;
import com.syntifi.casper.sdk.model.block.JsonBlock;

import org.springframework.batch.item.ItemProcessor;

public class BlockProcessor implements ItemProcessor<JsonBlock, JsonObject> {

    @Override
    public JsonObject process(JsonBlock casperBlock) throws Exception {
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
