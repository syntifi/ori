package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.TransferDTO;
import com.syntifi.ori.model.*;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
/**
 * Mapper for {@link Transfer} model and {@link TransferDTO}
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferMapper {

    private static final TransferDTO DEFAULT_DTO_VALUE = null;

    public static TransferDTO fromModel(Transfer model) {
        return model != null ? TransferDTO.builder()
                .hash(model.getHash())
                .timeStamp(model.getTimeStamp())
                .amount(model.getAmount())
                .fromHash(model.getFromAccount().getHash())
                .toHash(model.getToAccount().getHash())
                .blockHash(model.getBlock().getHash())
                .tokenSymbol(model.getToken().getSymbol())
                .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Transfer toModel(TransferDTO dto) {
        Chain chain = Chain.builder().name(dto.getChainName()).build();
        Token token = Token.builder().symbol(dto.getTokenSymbol()).chain(chain).build();
        Account fromAccount = dto.getFromHash() != null
                ? Account.builder().chain(chain).hash(dto.getFromHash()).build()
                : null;
        Account toAccount = dto.getToHash() != null
                ? Account.builder().chain(chain).hash(dto.getToHash()).build()
                : null;
        Block block = Block.builder().hash(dto.getBlockHash()).chain(chain).build();

        return Transfer.builder()
                .hash(dto.getHash())
                .timeStamp(dto.getTimeStamp())
                .amount(dto.getAmount())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .token(token)
                .block(block)
                .build();
    }
}
