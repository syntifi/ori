package com.syntifi.ori.chains.cspr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class CsprCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsprCrawlerApplication.class, args);
    }
}