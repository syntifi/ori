package com.syntifi.ori.chains.cspr.model;

import java.util.List;

import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.transfer.Transfer;
import com.syntifi.ori.chains.base.model.ChainData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@link ChainData} for Casper block chain data
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsprChainData implements ChainData<JsonBlock, Transfer> {

    private JsonBlock chainBlock;

    private List<Transfer> chainTransfers;
}
