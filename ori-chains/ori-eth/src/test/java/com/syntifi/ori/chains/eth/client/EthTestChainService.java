package com.syntifi.ori.chains.eth.client;

import java.util.List;

import com.syntifi.ori.chains.base.client.TestChainService;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;

public class EthTestChainService extends TestChainService<EthBlock, TransactionObject> {

    @Override
    public EthBlock getBlock() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<TransactionObject> getTransfers(String blockHash) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
