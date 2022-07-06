package com.syntifi.ori.chains.base.model;

import java.util.List;

import javax.validation.Valid;

import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TransferDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ORI compliant Data class
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OriData {
    /**
     * ori block DTO object
     * 
     * @param block ori block DTO object
     * @return ori block DTO object
     */
    @Valid
    private BlockDTO block;

    /**
     * list of ori transaction DTO object
     * 
     * @param transfers list of ori transaction DTO object
     * @return list of ori transaction DTO object
     */
    @Valid
    private List<TransferDTO> transfers;
}
