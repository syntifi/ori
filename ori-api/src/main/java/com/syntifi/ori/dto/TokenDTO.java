package com.syntifi.ori.dto;

import com.syntifi.ori.model.Token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    private static final TokenDTO DEFAULT_DTO_VALUE = null;

    private String symbol;

    private String name;

    private String protocol;

    public static TokenDTO fromModel(Token model) {
        return model != null ? new TokenDTO(model.getSymbol(), model.getName(), model.getProtocol())
                : DEFAULT_DTO_VALUE;
    }
}
