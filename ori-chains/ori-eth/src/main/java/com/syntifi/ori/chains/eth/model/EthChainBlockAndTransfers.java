package com.syntifi.ori.chains.eth.model;

import java.util.List;

import com.syntifi.ori.chains.base.model.ChainBlockAndTransfers;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EthChainBlockAndTransfers implements ChainBlockAndTransfers<EthBlock, EthTransaction> {

    private EthBlock chainBlock;

    private List<EthTransaction> chainTransfers;
}
