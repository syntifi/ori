package com.syntifi.ori.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OriAccountResponse {
    
    private String hash;

    private String publicKey;

    private String label;

    private String token;
}

