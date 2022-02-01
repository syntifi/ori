package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
                                                .tokenSymbol(model.getToken().getSymbol())
                                                .build()
                                : DEFAULT_DTO_VALUE;
        }

        public static Block toModel(BlockDTO dto) {
                Token token = Token.builder().symbol(dto.getTokenSymbol()).build();
                Block parentBlock = dto.getParent() != null ? Block.builder()
                                .hash(dto.getParent())
                                .token(token)
                                .build()
                                : null;

                return Block.builder()
                                .hash(dto.getHash())
                                .parent(parentBlock)
                                .hash(dto.getHash())
                                .timeStamp(dto.getTimeStamp())
                                .height(dto.getHeight())
                                .era(dto.getEra())
                                .root(dto.getRoot())
                                .validator(dto.getValidator())
                                .token(token)
                                .build();
        }

}
