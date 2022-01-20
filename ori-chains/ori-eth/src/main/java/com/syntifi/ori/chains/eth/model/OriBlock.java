package com.syntifi.ori.chains.eth.model;
import com.syntifi.ori.model.OriBlockPost;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OriBlock {
    
    private OriBlockPost block;

    private String parentBlock;
}