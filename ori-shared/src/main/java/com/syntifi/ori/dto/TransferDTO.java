package com.syntifi.ori.dto;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ori DTO for Transaction data
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
public class TransferDTO implements OriDTO {

    @NotNull
    private String hash;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private OffsetDateTime timeStamp;

    @NotNull
    private Double amount;

    @NotNull
    private String fromHash;

    private String toHash;

    @NotNull
    private String blockHash;

    @NotNull
    private String tokenSymbol;

    @NotNull
    private String chainName;
}
