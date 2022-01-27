package com.syntifi.ori.chains.base.model;

import java.util.List;

import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransactionDTO;

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
    private BlockDTO block;

    private List<TransactionDTO> transfers;
}
