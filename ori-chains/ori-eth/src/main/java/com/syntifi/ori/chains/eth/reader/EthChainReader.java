package com.syntifi.ori.chains.eth.reader;

import java.io.IOException;
import java.math.BigInteger;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.reader.AbstractChainReader;
import com.syntifi.ori.chains.eth.model.EthChainData;
import com.syntifi.ori.client.OriClient;

import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;

@Component
public class EthChainReader extends AbstractChainReader<Web3j, EthChainData> {

    public EthChainReader(Web3j chainService, OriClient oriClient,
            OriChainConfigProperties oriChainConfigProperties) {
        super(chainService, oriClient, oriChainConfigProperties);
    }

    @Override
    public EthChainData read() throws IOException, InterruptedException {
        EthChainData result = new EthChainData();

        BigInteger height = BigInteger.valueOf(getBlockHeight());
        DefaultBlockParameter blockParam = DefaultBlockParameter.valueOf(height);
        Request<?, EthBlock> blockRequest = getChainService().ethGetBlockByNumber(blockParam, true);
        EthBlock block = blockRequest.send();

        // Stop reading if no block data for given height
        if (block == null || block.getResult() == null) {
            return null;
        }

        result.setChainBlock(block);

        result.setChainTransfers(block.getResult().getTransactions()
                .stream()
                .map(transaction -> (TransactionObject) transaction.get())
                .collect(Collectors.toList()));

        nextItem();

        return result;
    }
}
