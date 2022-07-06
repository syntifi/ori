package com.syntifi.ori.chains.base.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import com.syntifi.ori.dto.TransferDTO;

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
    private double amount;
    private long timestamp;

    public TransferDTO toDTO() {
        return TransferDTO.builder()
                .hash(hash)
                .toHash(toHash)
                .fromHash(fromHash)
                .blockHash(blockHash)
                .timeStamp(OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()))
                .amount(amount) // TODO: Improve this
                .build();
    }
}
