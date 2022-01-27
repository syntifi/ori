package com.syntifi.ori.client;

import java.text.MessageFormat;
import java.util.List;

import com.google.gson.JsonObject;
import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

public class OriRestClient {

    private WebClient client;
    private String apiPrefix;

    public OriRestClient(String host) {
        this(host, "api", "v2");
    }

    public OriRestClient(String host, String apiEndpoint, String apiVersion) {
        try {
            WebClient test = WebClient
                    .builder()
                    .baseUrl(host)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            client = test;
        } catch (Exception e) {
            int x = 0;
        }
        apiPrefix = apiEndpoint + "/" + apiVersion;

    }

    public TokenDTO getToken(String tokenSymbol) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/token/{1}", apiPrefix, tokenSymbol))
                .retrieve()
                .bodyToMono(TokenDTO.class)
                .block();
    }

    public JsonObject postToken(TokenDTO token) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/token", apiPrefix))
                .body(Mono.just(token), TokenDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public BlockDTO getBlock(String tokenSymbol, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/block/{1}/hash/{2}", apiPrefix, tokenSymbol, hash))
                .retrieve()
                .bodyToMono(BlockDTO.class)
                .block();
    }

    public BlockDTO getLastBlock(String tokenSymbol) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/block/{1}/last", apiPrefix, tokenSymbol))
                .retrieve()
                .bodyToMono(BlockDTO.class)
                .block();
    }

    public JsonObject postBlock(String tokenSymbol, BlockDTO block)
            throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/block/{1}", apiPrefix, tokenSymbol))
                .body(Mono.just(block), BlockDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public JsonObject postBlocks(String tokenSymbol, List<BlockDTO> blocks)
            throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/block/{1}/multiple", apiPrefix, tokenSymbol))
                .body(Mono.just(blocks), List.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public AccountDTO getAccount(String tokenSymbol, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/account/{1}/hash/{2}", apiPrefix, tokenSymbol, hash))
                .retrieve()
                .bodyToMono(AccountDTO.class)
                .block();
    }

    public JsonObject postAccount(String tokenSymbol, AccountDTO account) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/account/{1}", apiPrefix, tokenSymbol))
                .body(Mono.just(account), BlockDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public TransactionDTO getTransfer(String tokenSymbol, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/transaction/{1}/hash/{2}", apiPrefix, tokenSymbol, hash))
                .retrieve()
                .bodyToMono(TransactionDTO.class)
                .block();
    }

    public JsonObject postTransfer(String tokenSymbol, TransactionDTO transfer) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/transaction/{1}", apiPrefix, tokenSymbol))
                .body(Mono.just(transfer), BlockDTO.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public JsonObject postTransfers(String tokenSymbol, List<TransactionDTO> transfers)
            throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/transaction/{1}/multiple", apiPrefix, tokenSymbol))
                .body(Mono.just(transfers), List.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }
}
