package com.syntifi.ori.chains.cspr.model;

import java.util.List;

import com.syntifi.ori.model.OriBlockPost;
import com.syntifi.ori.model.OriTransferPost;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OriBlockAndTransfers {
    private OriBlockPost block;

    private String parentBlock;

    private List<OriTransferPost> transfers;
    private List<String> from;
    private List<String> to;
    
}
