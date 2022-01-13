package com.syntifi.ori.client;

import java.text.MessageFormat;

import com.google.gson.JsonObject;
import com.syntifi.ori.model.OriAccountPost;
import com.syntifi.ori.model.OriAccountResponse;
import com.syntifi.ori.model.OriBlockPost;
import com.syntifi.ori.model.OriBlockResponse;
import com.syntifi.ori.model.OriToken;
import com.syntifi.ori.model.OriTransferPost;
import com.syntifi.ori.model.OriTransferResponse;

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
        client = WebClient
                .builder()
                .baseUrl(host)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        apiPrefix = apiEndpoint + "/" + apiVersion;

    }

    public OriToken getToken(String tokenSymbol) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/token/{1}", apiPrefix, tokenSymbol))
                .retrieve()
                .bodyToMono(OriToken.class)
                .block();
    }

    public JsonObject postToken(OriToken token) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/token", apiPrefix))
                .body(Mono.just(token), OriToken.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public OriBlockResponse getBlock(String tokenSymbol, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/block/{1}/hash/{2}", apiPrefix, tokenSymbol, hash))
                .retrieve()
                .bodyToMono(OriBlockResponse.class)
                .block();
    }

    public OriBlockResponse getLastBlock(String tokenSymbol) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/block/{1}/last", apiPrefix, tokenSymbol))
                .retrieve()
                .bodyToMono(OriBlockResponse.class)
                .block();
    }

    public JsonObject postBlock(String tokenSymbol, String parentHash, OriBlockPost block)
            throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/block/{1}/parent/{2}", apiPrefix, tokenSymbol, parentHash))
                .body(Mono.just(block), OriBlockPost.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public OriAccountResponse getAccount(String tokenSymbol, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/account/{1}/hash/{2}", apiPrefix, tokenSymbol, hash))
                .retrieve()
                .bodyToMono(OriAccountResponse.class)
                .block();
    }

    public JsonObject postAccount(String tokenSymbol, OriAccountPost account) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/block/{1}", apiPrefix, tokenSymbol))
                .body(Mono.just(account), OriBlockPost.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public OriTransferResponse getTransfer(String tokenSymbol, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/transaction/{1}/hash/{2}", apiPrefix, tokenSymbol, hash))
                .retrieve()
                .bodyToMono(OriTransferResponse.class)
                .block();
    }

    public JsonObject postTransfer(String tokenSymbol, String blockHash, String from,
            String to, OriTransferPost account) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/transaction/{1}/block/{2}/from/{3}/to/{4}",
                        apiPrefix, tokenSymbol, blockHash, from, to))
                .body(Mono.just(account), OriBlockPost.class)
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }
}
