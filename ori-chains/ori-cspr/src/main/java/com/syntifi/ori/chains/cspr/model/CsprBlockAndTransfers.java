package com.syntifi.ori.chains.cspr.model;

import java.util.List;

import com.syntifi.casper.sdk.model.block.JsonBlock;
import com.syntifi.casper.sdk.model.transfer.Transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CsprBlockAndTransfers {

    private JsonBlock block;

    private List<Transfer> transfers;

}
