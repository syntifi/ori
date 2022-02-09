package com.syntifi.ori.chains.base;

import java.io.IOException;

import javax.sql.DataSource;

import com.syntifi.ori.chains.base.client.MockChainService;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.client.mock.MockOriClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class MockChainConfig implements AbstractChainConfig<MockChainService> {

    @Value("${ori.host}")
    private String oriHost;

    @Value("${mock.node}")
    private String chainNode;

    @Value("${mock.port}")
    private int chainNodePort;

    @Value("${mock.token}")
    private String tokenSymbol;

    @Value("${mock.token.name}")
    private String tokenName;

    @Value("${mock.token.protocol}")
    private String tokenProtocol;

    @Value("${mock.block.zero.hash}")
    private String blockZeroHash;

    @Value("${mock.block.zero.height}")
    private long blockZeroHeight;

    @Value("${mock.batch.chunk.size}")
    private int chunkSize;

    @Bean
    @Override
    public OriClient oriClient() {
        return new MockOriClient();
    }

    @Bean
    @Override
    public MockChainService service() throws IOException {
        return new MockChainService();
    }

    // @Bean
    // @ConfigurationProperties("batch.datasource")
    // public DataSource batchDataSource() {
    //     return DataSourceBuilder.create().build();
    // }
}
