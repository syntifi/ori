package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.ChainDTO;
import com.syntifi.ori.model.Chain;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Mapper for {@link Chain} model and {@link ChainDTO}
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChainMapper {

    private static final ChainDTO DEFAULT_DTO_VALUE = null;

    public static ChainDTO fromModel(Chain model) {
        return model != null ? ChainDTO.builder()
                .name(model.getName())
                .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Chain toModel(ChainDTO dto) {
        return Chain.builder()
                .name(dto.getName())
                .build();
    }
}
