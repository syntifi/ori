package com.syntifi.ori.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OriTransferResponse {

    private String timeStamp;

    private String hash;

    private Double amount;

    private String from;

    private String to;

    private String block;
}

