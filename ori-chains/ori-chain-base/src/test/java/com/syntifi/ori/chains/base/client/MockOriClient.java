package com.syntifi.ori.chains.base.client;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

    public List<TokenDTO> tokens;

    public List<BlockDTO> blocks;

    public List<TransactionDTO> transactions;

    public List<AccountDTO> accounts;

    public MockOriClient() {
        this.tokens = new LinkedList<>();
        this.blocks = new LinkedList<>();
        this.transactions = new LinkedList<>();
        this.accounts = new LinkedList<>();
    }

    @Override
    public TokenDTO getToken(String tokenSymbol) throws WebClientResponseException {
        Optional<TokenDTO> token = this.tokens.stream().filter(t -> t.getSymbol().equals(tokenSymbol)).findFirst();

        if (token.isEmpty()) {
            throw WebClientResponseException.create(404, "Not found", null, null, Charset.defaultCharset());
        }

        return token.get();
    }

    @Override
    public JsonObject postToken(TokenDTO token) throws WebClientResponseException {
        this.tokens.add(token);
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public BlockDTO getBlock(String tokenSymbol, String hash) throws WebClientResponseException {
        Optional<BlockDTO> block = this.blocks.stream()
                .filter(t -> t.getTokenSymbol().equals(tokenSymbol) && t.getHash().equals(hash)).findFirst();

        if (block.isEmpty()) {
            throw WebClientResponseException.create(404, "Not found", null, null, Charset.defaultCharset());
        }

        return block.get();
    }

    @Override
    public BlockDTO getLastBlock(String tokenSymbol) throws WebClientResponseException {
        BlockDTO block = this.blocks
                .stream()
                .filter(t -> t.getTokenSymbol().equals(tokenSymbol))
                .max(Comparator.comparing(BlockDTO::getHeight))
                .orElseThrow(() -> WebClientResponseException.create(404, "Not found", null, null,
                        Charset.defaultCharset()));

        return block;
    }

    @Override
    public JsonObject postBlock(String tokenSymbol, BlockDTO block) throws WebClientResponseException {
        block.setTokenSymbol(tokenSymbol);
        this.blocks.add(block);
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public JsonObject postBlocks(String tokenSymbol, List<BlockDTO> blocks) throws WebClientResponseException {
        for (BlockDTO block : blocks) {
            block.setTokenSymbol(tokenSymbol);
            this.blocks.add(block);
        }
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public AccountDTO getAccount(String tokenSymbol, String hash) throws WebClientResponseException {
        Optional<AccountDTO> account = this.accounts.stream()
                .filter(t -> t.getTokenSymbol().equals(tokenSymbol) && t.getHash().equals(hash)).findFirst();

        if (account.isEmpty()) {
            throw WebClientResponseException.create(404, "Not found", null, null, Charset.defaultCharset());
        }

        return account.get();
    }

    @Override
    public JsonObject postAccount(String tokenSymbol, AccountDTO account) throws WebClientResponseException {
        account.setTokenSymbol(tokenSymbol);
        this.accounts.add(account);
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public TransactionDTO getTransfer(String tokenSymbol, String hash) throws WebClientResponseException {
        Optional<TransactionDTO> transfer = this.transactions.stream()
                .filter(t -> t.getTokenSymbol().equals(tokenSymbol) && t.getHash().equals(hash)).findFirst();

        if (transfer.isEmpty()) {
            throw WebClientResponseException.create(404, "Not found", null, null, Charset.defaultCharset());
        }

        return transfer.get();
    }

    @Override
    public JsonObject postTransfer(String tokenSymbol, TransactionDTO transfer) throws WebClientResponseException {
        transfer.setTokenSymbol(tokenSymbol);
        this.transactions.add(transfer);
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public JsonObject postTransfers(String tokenSymbol, List<TransactionDTO> transfers)
            throws WebClientResponseException {
        for (TransactionDTO transfer : transfers) {
            transfer.setTokenSymbol(tokenSymbol);
            this.transactions.add(transfer);
        }
        // TODO: What to return here?
        return new JsonObject();
    }
}
