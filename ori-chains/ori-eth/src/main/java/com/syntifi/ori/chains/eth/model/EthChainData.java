package com.syntifi.ori.chains.eth.model;

import java.util.List;

import com.syntifi.ori.chains.base.model.ChainData;

import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EthChainData implements ChainData<EthBlock, TransactionObject> {

    private EthBlock chainBlock;

    private List<TransactionObject> chainTransfers;
}
