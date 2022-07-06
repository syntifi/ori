package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.model.Chain;
import com.syntifi.ori.model.Token;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Mapper for {@link Token} model and {@link TokenDTO}
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenMapper {

    private static final TokenDTO DEFAULT_DTO_VALUE = null;

    public static TokenDTO fromModel(Token model) {
        return model != null ? TokenDTO.builder()
                .symbol(model.getSymbol())
                .name(model.getName())
                .chainName(model.getChain().getName())
                .quantization(model.getQuantization())
                .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Token toModel(TokenDTO dto) {
        Chain chain = Chain.builder().name(dto.getChainName()).build();
        return Token.builder()
                .symbol(dto.getSymbol())
                .name(dto.getName())
                .quantization(dto.getQuantization())
                .chain(chain)
                .build();
    }
}
