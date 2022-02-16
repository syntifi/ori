package com.syntifi.ori.client;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonObject;
import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 
 */
public class MockOriRestClient implements OriClient {

    private final List<TokenDTO> tokens;

    private final List<BlockDTO> blocks;

    private final List<TransactionDTO> transactions;

    private final List<AccountDTO> accounts;

    private boolean throwError = false;

    private HttpStatus errorCode;

    private String methodToThrow;

    public MockOriRestClient() {
        this.tokens = new LinkedList<>();
        this.blocks = new LinkedList<>();
        this.transactions = new LinkedList<>();
        this.accounts = new LinkedList<>();
        this.reset();
    }

    public void reset() {
        this.tokens.clear();
        this.blocks.clear();
        this.transactions.clear();
        this.accounts.clear();
        this.stopGeneratingErrorOnRequest();
    }

    public void generateErrorOnRequest(HttpStatus codeToThrow, String methodToThrow) {
        this.throwError = true;
        this.methodToThrow = methodToThrow;
        this.errorCode = codeToThrow;
    }

    public void stopGeneratingErrorOnRequest() {
        this.throwError = false;
        this.methodToThrow = null;
        this.errorCode = null;
    }

    @Override
    public TokenDTO getToken(String tokenSymbol) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.tokens.stream().filter(t -> t.getSymbol().equals(tokenSymbol)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postToken(TokenDTO token) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        this.tokens.add(token);
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public BlockDTO getBlock(String tokenSymbol, String hash) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.blocks.stream()
                .filter(t -> t.getTokenSymbol().equals(tokenSymbol) && t.getHash().equals(hash)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public BlockDTO getLastBlock(String tokenSymbol) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.blocks
                .stream()
                .filter(t -> t.getTokenSymbol().equals(tokenSymbol))
                .max(Comparator.comparing(BlockDTO::getHeight))
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postBlock(String tokenSymbol, BlockDTO block) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        block.setTokenSymbol(tokenSymbol);
        if (this.blocks.stream()
                .anyMatch(b -> b.getHash().equals(block.getHash()) && b.getTokenSymbol().equals(tokenSymbol))) {
            throw WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null,
                    Charset.defaultCharset());
        } else {
            this.blocks.add(block);
        }
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public JsonObject postBlocks(String tokenSymbol, List<BlockDTO> blocks) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        for (BlockDTO block : blocks) {
            block.setTokenSymbol(tokenSymbol);
            if (this.blocks.stream()
                    .anyMatch(b -> b.getHash().equals(block.getHash()) && b.getTokenSymbol().equals(tokenSymbol))) {
                throw WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null,
                        Charset.defaultCharset());
            } else {
                this.blocks.add(block);
            }
        }
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public AccountDTO getAccount(String tokenSymbol, String hash) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.accounts.stream()
                .filter(t -> t.getTokenSymbol().equals(tokenSymbol) && t.getHash().equals(hash)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postAccount(String tokenSymbol, AccountDTO account) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        account.setTokenSymbol(tokenSymbol);
        if (this.accounts.stream()
                .anyMatch(b -> b.getHash().equals(account.getHash()) && b.getTokenSymbol().equals(tokenSymbol))) {
            throw WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null,
                    Charset.defaultCharset());
        } else {
            this.accounts.add(account);
        }
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public TransactionDTO getTransfer(String tokenSymbol, String hash) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.transactions.stream()
                .filter(t -> t.getTokenSymbol().equals(tokenSymbol) && t.getHash().equals(hash)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postTransfer(String tokenSymbol, TransactionDTO transfer) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null, Charset.defaultCharset());
        }

        transfer.setTokenSymbol(tokenSymbol);
        if (this.transactions.stream()
                .anyMatch(b -> b.getHash().equals(transfer.getHash()) && b.getTokenSymbol().equals(tokenSymbol))) {
            throw WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null,
                    Charset.defaultCharset());
        } else {
            this.transactions.add(transfer);
        }
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public JsonObject postTransfers(String tokenSymbol, List<TransactionDTO> transfers)
            throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null, Charset.defaultCharset());
        }
        for (TransactionDTO transfer : transfers) {
            transfer.setTokenSymbol(tokenSymbol);
            if (this.transactions.stream()
                    .anyMatch(b -> b.getHash().equals(transfer.getHash()) && b.getTokenSymbol().equals(tokenSymbol))) {
                throw WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null,
                        Charset.defaultCharset());
            } else {
                this.transactions.add(transfer);
            }
        }
        // TODO: What to return here?
        return new JsonObject();
    }
}
