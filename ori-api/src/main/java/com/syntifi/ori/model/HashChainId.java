package com.syntifi.ori.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashChainId implements Serializable {

    @NotNull
    private String hash;

    @NotNull
    private String chain;
}
