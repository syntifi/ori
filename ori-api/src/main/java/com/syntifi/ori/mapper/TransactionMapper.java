package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.TransactionDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Block;
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
                .fromHash(model.getFromAccount().getHash())
                .toHash(model.getToAccount().getHash())
                .blockHash(model.getBlock().getHash())
                .tokenSymbol(model.getBlock().getToken().getSymbol())
                .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Transaction toModel(TransactionDTO dto, AccountRepository accountRepository,
            BlockRepository blockRepository) {
        Account fromAccount = getAccount(dto.getTokenSymbol(), dto.getFromHash(), accountRepository);
        Account toAccount = getAccount(dto.getTokenSymbol(), dto.getToHash(), accountRepository);
        Block block = getBlock(dto.getTokenSymbol(), dto.getBlockHash(), blockRepository);

        return Transaction.builder()
                .hash(dto.getHash())
                .timeStamp(dto.getTimeStamp())
                .amount(dto.getAmount())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .block(block)
                .build();
    }

    private static Account getAccount(String tokenSymbol, String hash, AccountRepository accountRepository) {
        Account account = accountRepository.findByHash(tokenSymbol, hash);
        // TODO: Throw with different exception and throw this @ controller level
        if (account == null) {
            throw new ORIException("Account not found", 404);
        }
        return account;
    }

    private static Block getBlock(String tokenSymbol, String hash, BlockRepository blockRepository) {
        Block block = blockRepository.findByHash(tokenSymbol, hash);
        // TODO: Throw with different exception and throw this @ controller level
        if (block == null) {
            throw new ORIException("Account not found", 404);
        }
        return block;
    }

}
