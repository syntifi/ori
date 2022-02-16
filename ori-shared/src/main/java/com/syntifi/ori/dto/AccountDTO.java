package com.syntifi.ori.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDTO implements OriDTO {

    @NotNull
    private String hash;

    private String publicKey;

    private String label;

    @NotNull
    private String tokenSymbol;
}
