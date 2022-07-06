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

    @Getter
    @Setter
    public static class HostProperties {

        /**
         * ORI client scheme
         * 
         * @return the scheme
         * @param scheme the scheme to set
         */
        private String scheme;

        /**
         * ORI client address
         * 
         * @return the address address
         * @param address the address address to set
         */
        private String address;

        /**
         * ORI client port
         * 
         * @return the port address
         * @param port the port address to set
         */
        private int port;
    }

    @Getter
    @Setter
    public static class ChainProperties {

        /**
         * Chain node properties
         * 
         * @return the chain node properties
         * @param node the chain node properties to set
         */
        private HostProperties node;

        /**
         * Chain token symbol
         * 
         * @return the chain token symbol
         * @param tokenSymbol the chain token symbol to set
         */
        private String tokenSymbol;

        /**
         * Chain token name
         * 
         * @return the chain token name
         * @param tokenName the chain token name to set
         */
        private String tokenName;

        /**
         * Chain token quantization
         *
         * @return the chain token minimal unit
         * @param tokenQuantitation the chain token name to set
         */
        private String tokenQuantization;

        /**
         * Chain name
         * 
         * @return the chain token protocol
         * @param chainName the chain token protocol to set
         */
        private String chainName;

        /**
         * Chain block zero hash
         * 
         * @return the chain block zero hash
         * @param blockZeroHash the chain block zero hash to set
         */
        private String blockZeroHash;

        /**
         * Chain block zero height
         * 
         * @return the block zero height
         * @param blockZeroHeight the block zero height to set
         */
        private long blockZeroHeight;
    }

    @Getter
    @Setter
    public static class BatchProperties {
        /**
         * Spring Batch chunk size
         * 
         * @return the Spring Batch chunk size value
         * @param chunkSize the Spring Batch chunk size value to set
         */
        private int chunkSize;
    }

    /**
     * ORI host properties
     * 
     * @return the ORI host properties
     * @param host the ORI host properties to set
     */
    private HostProperties host;

    /**
     * Chain settings
     * 
     * @return the Chain settings
     * @param chain the Chain settings value to set
     */
    private ChainProperties chain;

    /**
     * Spring Batch settings
     * 
     * @return the Spring Batch settings
     * @param batch the Spring Batch settings value to set
     */
    private BatchProperties batch;
}