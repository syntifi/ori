package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.TransactionDTO;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.model.Transaction;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
/**
 * Mapper for {@link Transaction} model and {@link TransactionDTO}
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionMapper {

    private static final TransactionDTO DEFAULT_DTO_VALUE = null;

    public static TransactionDTO fromModel(Transaction model) {
        return model != null ? TransactionDTO.builder()
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

    public static Transaction toModel(TransactionDTO dto) {
        Token token = Token.builder().symbol(dto.getTokenSymbol()).build();
        Account fromAccount = dto.getFromHash() != null
                ? Account.builder().token(token).hash(dto.getFromHash()).build()
                : null;
        Account toAccount = dto.getToHash() != null
                ? Account.builder().token(token).hash(dto.getToHash()).build()
                : null;
        Block block = Block.builder().hash(dto.getBlockHash()).token(token).build();

        return Transaction.builder()
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
