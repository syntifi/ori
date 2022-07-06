package com.syntifi.ori.client;

import java.text.MessageFormat;
import java.util.List;

import com.google.gson.JsonObject;
import com.syntifi.ori.dto.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

/**
 * Rest API {@link OriClient} implementation
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class OriRestClient implements OriClient {

    private WebClient client;
    private String apiPrefix;

    public OriRestClient(String host) {
        this(host, "api", "v2");
    }

    public OriRestClient(String host, String apiEndpoint, String apiVersion) {
        WebClient test = WebClient
                .builder()
                .baseUrl(host)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        client = test;

        apiPrefix = apiEndpoint + "/" + apiVersion;
    }

    public ChainDTO getChain(String chainName) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/chain/{1}", apiPrefix, chainName))
                .retrieve()
                .bodyToMono(ChainDTO.class)
                .block();
    }

    public JsonObject postChain(ChainDTO chain) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/chain", apiPrefix))
                .body(Mono.just(chain), ChainDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    @Override
    public TokenDTO getToken(String chainName, String tokenName) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/chain/{1}/token/{2}", apiPrefix, chainName, tokenName))
                .retrieve()
                .bodyToMono(TokenDTO.class)
                .block();
    }

    @Override
    public JsonObject postToken(String chainName, TokenDTO token) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/chain/{1}/token", apiPrefix, chainName))
                .body(Mono.just(token), TokenDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public BlockDTO getBlock(String chainName, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/chain/{1}/block/{2}", apiPrefix, chainName, hash))
                .retrieve()
                .bodyToMono(BlockDTO.class)
                .block();
    }

    public BlockDTO getLastBlock(String chainName) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/chain/{1}/block/last", apiPrefix, chainName))
                .retrieve()
                .bodyToMono(BlockDTO.class)
                .block();
    }

    public JsonObject postBlock(String chainName, BlockDTO block)
            throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/chain/{1}/block", apiPrefix, chainName))
                .body(Mono.just(block), BlockDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public JsonObject postBlocks(String chainName, List<BlockDTO> blocks)
            throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/chain/{1}/block/multiple", apiPrefix, chainName))
                .body(Mono.just(blocks), List.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public AccountDTO getAccount(String chainName, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/chain/{1}/account/{2}", apiPrefix, chainName, hash))
                .retrieve()
                .bodyToMono(AccountDTO.class)
                .block();
    }

    public JsonObject postAccount(String chainName, AccountDTO account) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/chain/{1}/account", apiPrefix, chainName))
                .body(Mono.just(account), BlockDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public TransferDTO getTransfer(String chainName, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/chain/{1}/transfer/{2}", apiPrefix, chainName, hash))
                .retrieve()
                .bodyToMono(TransferDTO.class)
                .block();
    }

    public JsonObject postTransfer(String chainName, TransferDTO transfer) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/chain/{1}/transfer", apiPrefix, chainName))
                .body(Mono.just(transfer), BlockDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public JsonObject postTransfers(String chainName, List<TransferDTO> transfers)
            throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/chain/{1}/transfer/multiple", apiPrefix, chainName))
                .body(Mono.just(transfers), List.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }
}
