package com.syntifi.ori.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.syntifi.ori.model.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private static final TransactionDTO DEFAULT_DTO_VALUE = null;

    private String hash;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ")
    private Date timeStamp;

    private Double amount;

    private String from;

    private String to;

    private String block;

    public static TransactionDTO fromModel(Transaction model) {
        return model != null ? new TransactionDTO(model.getHash(), model.getTimeStamp(), model.getAmount(),
                model.getFromAccount().getHash(), model.getToAccount().getHash(), model.getBlock().getHash())
                : DEFAULT_DTO_VALUE;
    }
}
