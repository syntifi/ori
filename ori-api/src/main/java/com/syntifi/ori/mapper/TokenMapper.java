package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.model.Token;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenMapper {
    
    private static final TokenDTO DEFAULT_DTO_VALUE = null;

    public static TokenDTO fromModel(Token model) {
        return model != null ? TokenDTO.builder()
                .symbol(model.getSymbol())
                .name(model.getName())
                .protocol(model.getProtocol())
                .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Token toModel(TokenDTO dto) {
        return Token.builder()
                .symbol(dto.getSymbol())
                .name(dto.getName())
                .protocol(dto.getProtocol())
                .build();
    }
}
