package com.syntifi.ori.chains.cspr;

import java.io.IOException;

import javax.sql.DataSource;

import com.syntifi.casper.sdk.service.CasperService;
import com.syntifi.ori.chains.base.AbstractChainConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class CsprChainConfig implements AbstractChainConfig<CasperService> {

    @Value("${ori.host}")
    private String oriHost;

    @Value("${cspr.node}")
    private String chainNode;

    @Value("${cspr.port}")
    private int chainNodePort;

    @Value("${cspr.token}")
    private String tokenSymbol;

    @Value("${cspr.token.name}")
    private String tokenName;

    @Value("${cspr.token.protocol}")
    private String tokenProtocol;

    @Value("${cspr.block.zero.hash}")
    private String blockZeroHash;

    @Value("${cspr.block.zero.height}")
    private long blockZeroHeight;

    @Value("${cspr.batch.chunk.size}")
    private int chunkSize;

    @Bean
    @Override
    public CasperService service() throws IOException {
        return CasperService.usingPeer(chainNode, chainNodePort);
    }

    // @Bean
    // @BatchDataSource
    // @ConfigurationProperties("batch.datasource")
    // public DataSource batchDataSource() {
    //     // return DataSourceBuilder.create().build();

    //     // batch.datasource.jdbc-url=jdbc:h2:mem:testdb
    //     // batch.datasource.driver-class-name=org.h2.Driver
    //     // batch.datasource.username=sa
    //     // batch.datasource.password=password
    //     return DataSourceBuilder.create().url("jdbc:h2:mem:testdb").driverClassName("org.h2.Driver").username("sa")
    //             .password("password").build();
    // }
}