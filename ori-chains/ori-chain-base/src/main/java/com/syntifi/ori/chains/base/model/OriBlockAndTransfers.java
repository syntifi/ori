package com.syntifi.ori.chains.base.model;

import java.util.List;

import com.syntifi.ori.model.OriBlockPost;
import com.syntifi.ori.model.OriTransferPost;

import lombok.Getter;
import lombok.Setter;

/**
 * ORI compliant Block and Transfer class
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertolace <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
@Getter
@Setter
public class OriBlockAndTransfers {
    private OriBlockPost block;

    private String parentBlock;

    private List<OriTransferPost> transfers;
    private List<String> from;
    private List<String> to;

}
