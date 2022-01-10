package com.syntifi.ori.client;

import java.text.MessageFormat;

import com.google.gson.JsonObject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    public JsonObject getToken(String tokenSymbol) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/token/{1}", apiPrefix, tokenSymbol))
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public JsonObject postToken(JsonObject token) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/token", apiPrefix))
                .bodyValue(token.toString())
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public JsonObject getBlock(String tokenSymbol, String hash) throws WebClientResponseException {
        return client
                .get()
                .uri(MessageFormat.format("{0}/block/{1}/hash/{2}", apiPrefix, tokenSymbol, hash))
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }

    public JsonObject postBlock(String tokenSymbol, String parentHash, JsonObject block) throws WebClientResponseException {
        return client
                .post()
                .uri(MessageFormat.format("{0}/block/{1}/parent/{2}", apiPrefix, tokenSymbol, parentHash))
                .bodyValue(block.toString())
                .retrieve()
                .bodyToMono(JsonObject.class)
                .block();
    }


}
