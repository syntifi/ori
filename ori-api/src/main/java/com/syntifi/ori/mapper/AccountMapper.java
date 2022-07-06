package com.syntifi.ori.mapper;

import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Chain;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Mapper for {@link Account} model and {@link AccountDTO}
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountMapper {

    private static final AccountDTO DEFAULT_DTO_VALUE = null;

    public static AccountDTO fromModel(Account model) {
        return model != null
                ? AccountDTO.builder()
                .hash(model.getHash())
                .publicKey(model.getPublicKey())
                .label(model.getLabel())
                .chainName(model.getChain().getName())
                .build()
                : DEFAULT_DTO_VALUE;
    }

    public static Account toModel(AccountDTO dto) {
        Chain chain = Chain.builder().name(dto.getChainName()).build();

        return Account.builder()
                .hash(dto.getHash())
                .publicKey(dto.getPublicKey())
                .label(dto.getLabel())
                .chain(chain)
                .build();
    }

}
