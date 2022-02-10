package com.syntifi.ori.chains.base;

import javax.sql.DataSource;

import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.Getter;

@Configuration
public class OriChainConfig {

    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    @Bean
    @ConditionalOnMissingBean
    protected OriClient getDefaultOriClient() {
        return new OriRestClient(oriChainConfigProperties.getHost());
    }

    @Bean
    @BatchDataSource
    @ConfigurationProperties(prefix = "ori.batch.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
