package com.syntifi.ori.model;

import java.util.Date;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Transaction model, only a couple of the fields available in the chain are needed 
 * 
 * @author Andre Bertolace 
 * @since 0.0.1
 */
public class Transaction {
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ")
    public Date timeStamp;

    @NotNull
    public String hash;

    @NotNull
    public String from;

    @NotNull
    public String to;

    @NotNull
    public Double amount;

    @NotNull
    public String blockHash;

    public Transaction() {};

    public Transaction(Date timeStamp, String hash, String from, String to, 
                    Double amount, String blockHash) {
        this.timeStamp = timeStamp;
        this.hash = hash;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.blockHash = blockHash;
    }
}
