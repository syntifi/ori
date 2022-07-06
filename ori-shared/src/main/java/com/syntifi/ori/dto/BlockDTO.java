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
 * Ori DTO for Block data
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
public class BlockDTO implements OriDTO {

    @NotNull
    private String hash;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private OffsetDateTime timeStamp;

    @NotNull
    private Long height;

    @NotNull
    private Long era;

    @NotNull
    private String root;

    @NotNull
    private String validator;

    private String parent;

    @NotNull
    private String chainName;
}