package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Chain;
import com.syntifi.ori.model.Token;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Mapper for {@link Block} model and {@link BlockDTO}
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockMapper {

    private static final BlockDTO DEFAULT_DTO_VALUE = null;

    public static BlockDTO fromModel(Block model) {
        String parentBlockHash = (model != null && model.getParent() != null) ? model.getParent().getHash()
                : null;

        return model != null
                ? BlockDTO.builder()
                .hash(model.getHash())
                .timeStamp(model.getTimeStamp())
                .height(model.getHeight())
                .era(model.getEra())
                .root(model.getRoot())
                .validator(model.getValidator())
                .parent(parentBlockHash)
                .chainName(model.getChain().getName())
                .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Block toModel(BlockDTO dto) {
        Chain chain = Chain.builder().name(dto.getChainName()).build();
        Block parentBlock = dto.getParent() != null ? Block.builder()
                .hash(dto.getParent())
                .chain(chain)
                .build()
                : null;

        return Block.builder()
                .chain(chain)
                .hash(dto.getHash())
                .parent(parentBlock)
                .hash(dto.getHash())
                .timeStamp(dto.getTimeStamp())
                .height(dto.getHeight())
                .era(dto.getEra())
                .root(dto.getRoot())
                .validator(dto.getValidator())
                .build();
    }

}
