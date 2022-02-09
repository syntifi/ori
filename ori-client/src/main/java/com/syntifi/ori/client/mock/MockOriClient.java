package com.syntifi.ori.client.mock;

import java.util.List;

import com.google.gson.JsonObject;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 
 */
public class MockOriClient implements OriClient {

    @Override
    public TokenDTO getToken(String tokenSymbol) throws WebClientResponseException {
        return TokenDTO.builder().symbol(tokenSymbol).name(tokenSymbol).build();
    }

    @Override
    public JsonObject postToken(TokenDTO token) throws WebClientResponseException {
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public BlockDTO getBlock(String tokenSymbol, String hash) throws WebClientResponseException {
        return BlockDTO.builder()
                .hash(hash)
                .tokenSymbol(tokenSymbol)
                .build();
    }

    @Override
    public BlockDTO getLastBlock(String tokenSymbol) throws WebClientResponseException {
        return BlockDTO.builder()
                .hash("hash")
                .height(0L)
                .tokenSymbol(tokenSymbol)
                .build();
    }

    @Override
    public JsonObject postBlock(String tokenSymbol, BlockDTO block) throws WebClientResponseException {
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public JsonObject postBlocks(String tokenSymbol, List<BlockDTO> blocks) throws WebClientResponseException {
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public AccountDTO getAccount(String tokenSymbol, String hash) throws WebClientResponseException {
        return AccountDTO.builder()
                .hash(hash)
                .tokenSymbol(tokenSymbol)
                .build();
    }

    @Override
    public JsonObject postAccount(String tokenSymbol, AccountDTO account) throws WebClientResponseException {
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public TransactionDTO getTransfer(String tokenSymbol, String hash) throws WebClientResponseException {
        return TransactionDTO.builder()
                .hash(hash)
                .tokenSymbol(tokenSymbol)
                .build();
    }

    @Override
    public JsonObject postTransfer(String tokenSymbol, TransactionDTO transfer) throws WebClientResponseException {
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public JsonObject postTransfers(String tokenSymbol, List<TransactionDTO> transfers)
            throws WebClientResponseException {
        // TODO: What to return here?
        return new JsonObject();
    }

}
