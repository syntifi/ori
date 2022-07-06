package com.syntifi.ori.client;

import java.util.List;

import com.google.gson.JsonObject;
import com.syntifi.ori.dto.*;

import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Interface for Ori API client implementation
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public interface OriClient {

        public ChainDTO getChain(String chainName) throws WebClientResponseException;

        public JsonObject postChain(ChainDTO chain) throws WebClientResponseException;

        public TokenDTO getToken(String chainName, String tokenName) throws WebClientResponseException;

        public JsonObject postToken(String chainName, TokenDTO token) throws WebClientResponseException;

        public BlockDTO getBlock(String chainName, String hash) throws WebClientResponseException;

        public BlockDTO getLastBlock(String chainName) throws WebClientResponseException;

        public JsonObject postBlock(String chainName, BlockDTO block)
                        throws WebClientResponseException;

        public JsonObject postBlocks(String chainName, List<BlockDTO> blocks)
                        throws WebClientResponseException;

        public AccountDTO getAccount(String chainName, String hash) throws WebClientResponseException;

        public JsonObject postAccount(String chainName, AccountDTO account) throws WebClientResponseException;

        public TransferDTO getTransfer(String chainName, String hash) throws WebClientResponseException;

        public JsonObject postTransfer(String chainName, TransferDTO transfer) throws WebClientResponseException;

        public JsonObject postTransfers(String chainName, List<TransferDTO> transfers)
                        throws WebClientResponseException;
}
