package com.syntifi.ori.client;

import java.util.List;

import com.google.gson.JsonObject;
import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 
 */
public interface OriClient {

    public TokenDTO getToken(String tokenSymbol) throws WebClientResponseException;

    public JsonObject postToken(TokenDTO token) throws WebClientResponseException;

    public BlockDTO getBlock(String tokenSymbol, String hash) throws WebClientResponseException;

    public BlockDTO getLastBlock(String tokenSymbol) throws WebClientResponseException;

    public JsonObject postBlock(String tokenSymbol, BlockDTO block)
            throws WebClientResponseException;

    public JsonObject postBlocks(String tokenSymbol, List<BlockDTO> blocks)
            throws WebClientResponseException;

    public AccountDTO getAccount(String tokenSymbol, String hash) throws WebClientResponseException;

    public JsonObject postAccount(String tokenSymbol, AccountDTO account) throws WebClientResponseException;

    public TransactionDTO getTransfer(String tokenSymbol, String hash) throws WebClientResponseException;

    public JsonObject postTransfer(String tokenSymbol, TransactionDTO transfer) throws WebClientResponseException;

    public JsonObject postTransfers(String tokenSymbol, List<TransactionDTO> transfers)
            throws WebClientResponseException;
}
