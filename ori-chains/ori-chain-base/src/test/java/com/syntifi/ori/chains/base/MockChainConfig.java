package com.syntifi.ori.chains.base;

import java.io.IOException;

import javax.sql.DataSource;

import com.syntifi.ori.chains.base.client.MockChainService;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.client.mock.MockOriClient;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class MockChainConfig {

    @Bean
    @Primary
    protected OriClient getOriClient() {
        return new MockOriClient();
    }

    @Bean
    protected MockChainService getServiceInstance() throws IOException {
        return new MockChainService();
    }

    @Bean
    @BatchDataSource
    @ConfigurationProperties(prefix = "ori.batch.datasource")
    public DataSource dataSource() {
        DataSource ds = DataSourceBuilder.create().build();
        return ds;
    }
}
