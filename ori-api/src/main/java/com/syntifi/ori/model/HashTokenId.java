package com.syntifi.ori.model;

import java.io.Serializable;

import io.smallrye.common.constraint.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashTokenId implements Serializable {

    @NotNull
    private String hash;

    @NotNull
    private String token;
}
