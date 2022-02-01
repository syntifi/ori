package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Token;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountMapper {

    private static final AccountDTO DEFAULT_DTO_VALUE = null;

    public static AccountDTO fromModel(Account model) {
        return model != null
                ? AccountDTO.builder()
                        .hash(model.getHash())
                        .publicKey(model.getPublicKey())
                        .label(model.getLabel())
                        .tokenSymbol(model.getToken().getSymbol())
                        .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Account toModel(AccountDTO dto) {
        Token token = Token.builder().symbol(dto.getTokenSymbol()).build();
        
        return Account.builder()
                .hash(dto.getHash())
                .publicKey(dto.getPublicKey())
                .label(dto.getLabel())
                .token(token)
                .build();
    }

}
