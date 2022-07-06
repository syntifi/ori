package com.syntifi.ori.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Ori DTO for Token data
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 *
 * @since 0.2.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChainDTO {

    @NotNull
    private String name;
}
