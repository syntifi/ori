package com.syntifi.ori.chains.base;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Read the specific configuration to feed the crawler
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertolace <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ori")
public class OriChainConfigProperties {

    private String host;

    private String chainNode;

    private int chainNodePort;

    private String chainTokenSymbol;

    private String chainTokenName;

    private String chainTokenProtocol;

    private String chainBlockZeroHash;

    private long chainBlockZeroHeight;

    private int batchChunkSize;
}