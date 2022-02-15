package com.syntifi.ori.chains.base.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MockChainTransfer {
    private String hash;
    private String toHash;
    private String fromHash;
    private String blockHash;
    private long amount;
}
