package com.syntifi.ori.chains.base;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Read the specific configuration to feed the crawler
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ori")
public class OriChainConfigProperties {

    /**
     * ORI client host
     * 
     * @return the host address
     * @param host the host address to set
     */
    private String host;

    /**
     * Chain node address
     * 
     * @return the chain node address
     * @param chainNode the chain node address to set
     */
    private String chainNode;

    /**
     * Chain node port
     * 
     * @return the chain node port
     * @param chainNodePort the chain node port to set
     */
    private int chainNodePort;

    /**
     * Chain token symbol
     * 
     * @return the chain token symbol
     * @param chainTokenSymbol the chain token symbol to set
     */
    private String chainTokenSymbol;

    /**
     * Chain token name
     * 
     * @return the chain token name
     * @param chainTokenName the chain token name to set
     */
    private String chainTokenName;

    /**
     * Chain token protocol
     * 
     * @return the chain token protocol
     * @param chainTokenProtocol the chain token protocol to set
     */
    private String chainTokenProtocol;

    /**
     * Chain block zero hash
     * 
     * @return the chain block zero hash
     * @param chainBlockZeroHash the chain block zero hash to set
     */
    private String chainBlockZeroHash;

    /**
     * Chain block zero height
     * 
     * @return the block zero height
     * @param chainBlockZeroHeight the block zero height to set
     */
    private long chainBlockZeroHeight;

    /**
     * Spring Batch chunk size
     * 
     * @return the Spring Batch chunk size value
     * @param batchChunkSize the Spring Batch chunk size value to set
     */
    private int batchChunkSize;
}