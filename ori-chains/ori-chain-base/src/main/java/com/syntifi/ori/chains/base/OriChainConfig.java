package com.syntifi.ori.chains.base;

import javax.sql.DataSource;

import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.client.OriRestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Chain Configuration for properties loading and bean definitions
 * 
 * Can and should be extended by chain crawlers to inject its beans (ie:
 * its specific api client)
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@ComponentScan(basePackageClasses = OriChainConfig.class)
@EnableConfigurationProperties(OriChainConfigProperties.class)
@PropertySource("classpath:ori-chain-default-application.properties")
public class OriChainConfig {

    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    @Bean
    @ConditionalOnMissingBean
    protected OriClient getOriClient() {
        return new OriRestClient(oriChainConfigProperties.getHost());
    }

    @Bean
    @BatchDataSource
    @ConfigurationProperties(prefix = "ori.batch.datasource")
    protected DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
