package com.syntifi.ori.chains.eth.reader;

import java.io.IOException;
import java.math.BigInteger;

import com.syntifi.ori.client.OriRestClient;

import org.springframework.batch.item.ItemReader;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

public class BlockAndTransfersReader implements ItemReader<EthBlock> {

    private Long blockHeight;
    private String tokenSymbol;
    private Web3j eth;
    private OriRestClient oriRestClient;

    public BlockAndTransfersReader(Web3j ethService,
            OriRestClient oriRestClient,
            String tokenSymbol) {
        this.eth = ethService;
        this.oriRestClient = oriRestClient;
        this.tokenSymbol = tokenSymbol;
        initialize();
    }

    private void initialize() {
        try {
            blockHeight = oriRestClient.getLastBlock(tokenSymbol).getHeight()+1;
        } catch (WebClientResponseException e) {
            if (e.getRawStatusCode() == 404) {
                blockHeight = 0L;
            }
        }
    }

    private boolean nextItem() {
        blockHeight += 1;
        return true;
    }

    // READ should return null if next item is not found
    @Override
    public EthBlock read() throws IOException, InterruptedException {
        if (blockHeight == null)
            return null;
        BigInteger height = BigInteger.valueOf(blockHeight);
        var blockParam = DefaultBlockParameter.valueOf(height);
        EthBlock block = eth.ethGetBlockByNumber(blockParam, true).send();
        nextItem();
        return block;
    }

}
