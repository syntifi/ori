package com.syntifi.ori.chains.cspr.writer;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class BlockWriter implements ItemWriter<Block> {

    private final Token token;
    private final String restHttp;

    public BlockWriter(Token token, String restHttp) {
        this.token = token;
        this.restHttp = restHttp;
    }

    @Bean
    public WebClient localApiClient() {
        return WebClient.create(restHttp);
    }

    @Override
    public void write(List<? extends Block> blocks) {
        WebClient webclient = localApiClient();
        for (Block block : blocks) {
            webclient.post()
            .uri("/block/" + token.getSymbol() + "/parent/" + block.getParent())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(Mono.just(block), Block.class)
            .retrieve()
            .bodyToMono(Block.class);
        }

    }
}
