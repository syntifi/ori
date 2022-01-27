package com.syntifi.ori.chains.cspr.model;

import java.util.List;

import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.transfer.Transfer;
import com.syntifi.ori.chains.base.model.ChainBlockAndTransfers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CsprChainBlockAndTransfers implements ChainBlockAndTransfers<JsonBlock, Transfer> {

    private JsonBlock chainBlock;

    private List<Transfer> chainTransfers;
}
