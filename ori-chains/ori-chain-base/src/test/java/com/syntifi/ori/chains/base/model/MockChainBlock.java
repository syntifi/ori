package com.syntifi.ori.chains.base.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MockChainBlock {
    private String hash;
    private long height;
    private String parentHash;
    private long timestamp; 
}
