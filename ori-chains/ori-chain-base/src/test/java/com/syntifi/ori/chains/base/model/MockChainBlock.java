package com.syntifi.ori.chains.base.model;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import com.syntifi.ori.dto.BlockDTO;

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

    public BlockDTO toDTO() {
        return BlockDTO.builder()
                .era(0L) // for constraint
                .root("root") // for constraint
                .validator("validator") // for constraint
                .hash(hash)
                .height(height)
                .parent(parentHash)
                .timeStamp(OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()))
                .build();
    }
}
