package com.syntifi.ori.rest;

import javax.inject.Singleton;
import javax.ws.rs.Path;

import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Ori Rest Api resource endpoints for {@link BlockRestAPI},
 * {@link TransactionRestAPI}, {@link AccountRestAPI}, {@link TokenRestAPI} and
 * {@link TransactionMonitorAPI}
 * 
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Singleton
@Unremovable
@RegisterForReflection
public class RestApiResource {
    private final BlockRestAPI blockAPI;
    private final TransactionRestAPI transactionAPI;
    private final AccountRestAPI accountAPI;
    private final TokenRestAPI tokenAPI;
    private final TransactionMonitorAPI monitorAPI;

    public RestApiResource(BlockRestAPI blockRestAPI,
            TransactionRestAPI transactionRestAPI,
            AccountRestAPI accountRestAPI,
            TokenRestAPI tokenRestAPI,
            TransactionMonitorAPI transactionMonitorAPI) {
        this.blockAPI = blockRestAPI;
        this.transactionAPI = transactionRestAPI;
        this.accountAPI = accountRestAPI;
        this.tokenAPI = tokenRestAPI;
        this.monitorAPI = transactionMonitorAPI;
    }

    @Path("/block")
    public BlockRestAPI getBlockResource() {
        return blockAPI;
    }

    @Path("/transaction")
    public TransactionRestAPI getTransactionResource() {
        return transactionAPI;
    }

    @Path("/account")
    public AccountRestAPI getAccountResource() {
        return accountAPI;
    }

    @Path("/token")
    public TokenRestAPI getTokenResource() {
        return tokenAPI;
    }

    @Path("/monitor")
    public TransactionMonitorAPI getMonitorResource() {
        return monitorAPI;
    }
}
