package com.syntifi.ori.chains.cspr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ComponentScan
public class CsprConfig {

    @Value("${ori.rest.api}")
    private String oriRestHttp;

    @Bean
    public WebClient oriRestClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(oriRestHttp)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}