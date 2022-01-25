package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.TransactionDTO;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.AccountRepository;
import com.syntifi.ori.repository.BlockRepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionMapper {
    
    private static final TransactionDTO DEFAULT_DTO_VALUE = null;

    public static TransactionDTO fromModel(Transaction model) {
        return model != null ? TransactionDTO.builder()
                .hash(model.getHash())
                .timeStamp(model.getTimeStamp())
                .amount(model.getAmount())
                .from(model.getFromAccount().getHash())
                .to(model.getToAccount().getHash())
                .blockHash(model.getBlock().getHash())
                .tokenSymbol(model.getBlock().getToken().getSymbol())
                .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Transaction toModel(TransactionDTO dto, AccountRepository accountRepository,
            BlockRepository blockRepository) {
        return Transaction.builder()
                .hash(dto.getHash())
                .timeStamp(dto.getTimeStamp())
                .amount(dto.getAmount())
                .fromAccount(accountRepository.findByHash(dto.getFrom()))
                .toAccount(accountRepository.findByHash(dto.getTo()))
                .block(blockRepository.findByHash(dto.getTokenSymbol(), dto.getBlockHash()))
                .build();
    }
}
