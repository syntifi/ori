package com.syntifi.ori.chains.eth.reader;

import java.io.IOException;
import java.math.BigInteger;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.reader.AbstractChainReader;
import com.syntifi.ori.chains.eth.EthChainConfig;
import com.syntifi.ori.chains.eth.model.EthChainData;
import com.syntifi.ori.client.OriClient;

import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;

@Component
public class EthChainReader
        extends AbstractChainReader<Web3j, EthChainData> {

    public EthChainReader(Web3j chainService, OriClient oriClient,
            EthChainConfig chainConfig) {
        super(chainService, oriClient, chainConfig);
    }

    // READ should return null if next item is not found
    @Override
    public EthChainData read() throws IOException, InterruptedException {
        if (getBlockHeight() == null) {
            return null;
        }

        EthChainData result = new EthChainData();

        BigInteger height = BigInteger.valueOf(getBlockHeight());
        DefaultBlockParameter blockParam = DefaultBlockParameter.valueOf(height);
        EthBlock block = getChainService().ethGetBlockByNumber(blockParam, true).send();

        result.setChainBlock(block);

        result.setChainTransfers(block.getResult().getTransactions()
                .stream()
                .map(transaction -> (TransactionObject) transaction.get())
                .collect(Collectors.toList()));

        nextItem();

        return result;
    }
}
