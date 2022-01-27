package com.syntifi.ori.chains.eth.reader;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.reader.AbstractChainBlockAndTransfersReader;
import com.syntifi.ori.chains.eth.model.EthChainBlockAndTransfers;
import com.syntifi.ori.client.OriRestClient;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;

public class EthChainBlockAndTransfersReader
        extends AbstractChainBlockAndTransfersReader<Web3j, EthChainBlockAndTransfers> {

    public EthChainBlockAndTransfersReader(Web3j chainService, OriRestClient oriRestClient, String tokenSymbol) {
        super(chainService, oriRestClient, tokenSymbol);
    }

    // READ should return null if next item is not found
    @Override
    public EthChainBlockAndTransfers read() throws IOException, InterruptedException {
        if (getBlockHeight() == null)
            return null;
        EthChainBlockAndTransfers out = new EthChainBlockAndTransfers();
        BigInteger height = BigInteger.valueOf(getBlockHeight());
        DefaultBlockParameter blockParam = DefaultBlockParameter.valueOf(height);
        EthBlock block = getChainService().ethGetBlockByNumber(blockParam, true).send();
        out.setChainBlock(block);
        List<TransactionResult> transactions = block.getResult().getTransactions();
        out.setChainTransfers(transactions
                .stream()
                .map((transaction) -> (TransactionObject) transaction.get())
                .collect(Collectors.toList()));
        nextItem();
        return out;
    }
}
