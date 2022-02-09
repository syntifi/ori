package com.syntifi.ori.chains.base.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MockChainData implements ChainData<MockChainBlock, MockChainTransfer> {
    private MockChainBlock chainBlock;
    private List<MockChainTransfer> chainTransfers;
}
