package com.syntifi.ori.dto;

import com.syntifi.ori.model.Account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private static final AccountDTO DEFAULT_DTO_VALUE = null;

    private String hash;

    private String publicKey;

    private String label;

    private String token;

    public static AccountDTO fromModel(Account model) {
        return model != null
                ? new AccountDTO(model.getHash(), model.getPublicKey(), model.getLabel(), model.getToken().getSymbol())
                : DEFAULT_DTO_VALUE;
    }
}
