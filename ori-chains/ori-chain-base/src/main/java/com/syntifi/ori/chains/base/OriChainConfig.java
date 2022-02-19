package com.syntifi.ori.chains.base;

import javax.sql.DataSource;

import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.client.OriRestClient;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
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
 * <p>
 * Can and should be extended by chain crawlers to inject its beans (ie:
 * its specific api client)
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@ComponentScan(basePackageClasses = OriChainConfig.class)
@EnableConfigurationProperties(OriChainConfigProperties.class)
@PropertySource("classpath:ori-chain-default-application.properties")
public class OriChainConfig {

    /**
     * {@link OriChainConfigProperties} reference
     *
     * @return the {@link OriChainConfigProperties} object
     */
    @Getter(value = AccessLevel.PROTECTED)
    @Autowired
    private OriChainConfigProperties oriChainConfigProperties;

    /**
     * Getter for the default {@link OriClient} bean
     *
     * @return the OriClient bean
     */
    @Bean
    @ConditionalOnMissingBean
    protected OriClient getOriClient() {
        return new OriRestClient(String.format("%s://%s:%s", oriChainConfigProperties.getHost().getScheme(),
                oriChainConfigProperties.getHost().getAddress(), oriChainConfigProperties.getHost().getPort()));
    }

    @Bean
    @ConfigurationProperties(prefix = "ori.batch.datasource")
    public DataSourceProperties springBatchDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Getter for Spring Batch {@link DataSource}
     *
     * @return the bean of the datasource for Spring Batch database
     */
    @Bean
    @BatchDataSource
    protected HikariDataSource dataSource(@Qualifier("springBatchDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}
