package com.syntifi.ori.client;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonObject;
import com.syntifi.ori.dto.*;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Mock {@link OriClient} for general testing
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class MockOriRestClient implements OriClient {

    private final List<ChainDTO> chains;

    private final List<TokenDTO> tokens;

    private final List<BlockDTO> blocks;

    private final List<TransferDTO> transactions;

    private final List<AccountDTO> accounts;

    private boolean throwError = false;

    private HttpStatus errorCode;

    private String methodToThrow;

    public MockOriRestClient() {
        this.chains= new LinkedList<>();
        this.tokens = new LinkedList<>();
        this.blocks = new LinkedList<>();
        this.transactions = new LinkedList<>();
        this.accounts = new LinkedList<>();
        this.reset();
    }

    public void reset() {
        this.chains.clear();
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
    public ChainDTO getChain(String chainName) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.chains.stream().filter(t -> t.getName().equals(chainName)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postChain(ChainDTO chain) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        this.chains.add(chain);
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public TokenDTO getToken(String chainName, String tokenName) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.tokens.stream().filter(t -> t.getChainName().equals(chainName) && t.getSymbol().equals(tokenName)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postToken(String chainName, TokenDTO token) throws WebClientResponseException {
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
    public BlockDTO getBlock(String chainName, String hash) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.blocks.stream()
                .filter(t -> t.getChainName().equals(chainName) && t.getHash().equals(hash)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public BlockDTO getLastBlock(String chainName) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.blocks
                .stream()
                .filter(t -> t.getChainName().equals(chainName))
                .max(Comparator.comparing(BlockDTO::getHeight))
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postBlock(String chainName, BlockDTO block) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        block.setChainName(chainName);
        if (this.blocks.stream()
                .anyMatch(b -> b.getHash().equals(block.getHash()) && b.getChainName().equals(chainName))) {
            throw WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null,
                    Charset.defaultCharset());
        } else {
            this.blocks.add(block);
        }
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public JsonObject postBlocks(String chainName, List<BlockDTO> blocks) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        for (BlockDTO block : blocks) {
            block.setChainName(chainName);
            if (this.blocks.stream()
                    .anyMatch(b -> b.getHash().equals(block.getHash()) && b.getChainName().equals(chainName))) {
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
    public AccountDTO getAccount(String chainName, String hash) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.accounts.stream()
                .filter(t -> t.getChainName().equals(chainName) && t.getHash().equals(hash)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postAccount(String chainName, AccountDTO account) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        account.setChainName(chainName);
        if (this.accounts.stream()
                .anyMatch(b -> b.getHash().equals(account.getHash()) && b.getChainName().equals(chainName))) {
            throw WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null,
                    Charset.defaultCharset());
        } else {
            this.accounts.add(account);
        }
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public TransferDTO getTransfer(String chainName, String hash) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null,
                    Charset.defaultCharset());
        }

        return this.transactions.stream()
                .filter(t -> t.getTokenSymbol().equals(chainName) && t.getHash().equals(hash)).findFirst()
                .orElseThrow(
                        () -> WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found", null, null,
                                Charset.defaultCharset()));
    }

    @Override
    public JsonObject postTransfer(String chainName, TransferDTO transfer) throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null, Charset.defaultCharset());
        }

        transfer.setTokenSymbol(chainName);
        if (this.transactions.stream()
                .anyMatch(b -> b.getHash().equals(transfer.getHash()) && b.getTokenSymbol().equals(chainName))) {
            throw WebClientResponseException.create(HttpStatus.CONFLICT.value(), "Conflict", null, null,
                    Charset.defaultCharset());
        } else {
            this.transactions.add(transfer);
        }
        // TODO: What to return here?
        return new JsonObject();
    }

    @Override
    public JsonObject postTransfers(String chainName, List<TransferDTO> transfers)
            throws WebClientResponseException {
        if (throwError && methodToThrow.equals(new Throwable().getStackTrace()[0].getMethodName())) {
            throw WebClientResponseException.create(this.errorCode.value(), this.errorCode.getReasonPhrase(), null,
                    null, Charset.defaultCharset());
        }
        for (TransferDTO transfer : transfers) {
            transfer.setTokenSymbol(chainName);
            if (this.transactions.stream()
                    .anyMatch(b -> b.getHash().equals(transfer.getHash()) && b.getTokenSymbol().equals(chainName))) {
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
