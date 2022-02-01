package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.repository.BlockRepository;
import com.syntifi.ori.repository.TokenRepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockMapper {

    private static final BlockDTO DEFAULT_DTO_VALUE = null;

    public static BlockDTO fromModel(Block model) {
        return model != null
                ? BlockDTO.builder()
                        .hash(model.getHash())
                        .timeStamp(model.getTimeStamp())
                        .height(model.getHeight())
                        .era(model.getEra())
                        .root(model.getRoot())
                        .validator(model.getValidator())
                        .parent(model.getParent() != null ? model.getParent().getHash() : null)
                        .tokenSymbol(model.getToken().getSymbol())
                        .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Block toModel(BlockDTO dto, BlockRepository blockRepository, TokenRepository tokenRepository) {
        return Block.builder()
                .hash(dto.getHash())
                .parent(dto.getParent() != null ? blockRepository.findByHash(dto.getTokenSymbol(), dto.getParent())
                        : null)
                .hash(dto.getHash())
                .timeStamp(dto.getTimeStamp())
                .height(dto.getHeight())
                .era(dto.getEra())
                .root(dto.getRoot())
                .validator(dto.getValidator())
                .token(tokenRepository.findBySymbol(dto.getTokenSymbol()))
                .build();
    }

}
