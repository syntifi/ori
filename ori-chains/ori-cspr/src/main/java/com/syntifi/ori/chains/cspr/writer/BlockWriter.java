package com.syntifi.ori.chains.cspr.writer;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import com.google.gson.JsonObject;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class BlockWriter implements ItemWriter<JsonObject> {

    private final String token;
    private final String restHttp;

    public BlockWriter(String token, String restHttp) {
        this.token = token;
        this.restHttp = restHttp;
    }

    @Bean
    public WebClient localApiClient() {
        return WebClient.create(restHttp);
    }

    @Override
    public void write(List<? extends JsonObject> blocks) {
        WebClient webclient = localApiClient();
        for (JsonObject block : blocks) {
            String parentBlock = block.get("parent").toString();
            block.remove("parent");
            webclient.post()
                    .uri("/api/v2/block/" + token + "/parent/" + parentBlock)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(block), JsonObject.class)
                    .retrieve()
                    .bodyToMono(JsonObject.class);
        }

    }
}
