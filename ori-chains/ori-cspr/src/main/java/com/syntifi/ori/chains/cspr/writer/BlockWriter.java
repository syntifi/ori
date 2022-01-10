package com.syntifi.ori.chains.cspr.writer;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import org.springframework.batch.item.ItemWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class BlockWriter implements ItemWriter<JsonObject> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private WebClient oriRestClient;
    private String tokenName;
    private String oriRestBlockParent;

    public BlockWriter(WebClient client, String token, String restMethod) {
        oriRestClient = client;
        tokenName = token;
        oriRestBlockParent = restMethod;
    }

    @Override
    public void write(List<? extends JsonObject> blocks) {
        for (JsonObject block : blocks) {
            String parentBlock = block.get("parent").getAsString();
            block.remove("parent");
            JsonObject response = oriRestClient.post()
                    .uri(MessageFormat.format(oriRestBlockParent, tokenName, parentBlock))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(block.toString())
                    .retrieve()
                    .bodyToMono(JsonObject.class)
                    .block();
            logger.info(response.toString());
        }
    }
}
