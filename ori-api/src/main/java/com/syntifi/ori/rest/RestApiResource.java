package com.syntifi.ori.rest;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;

@Singleton
public class RestApiResource {
    private final BlockRestAPI blockAPI;
    private final TransactionRestAPI transactionAPI;
    private final AccountRestAPI accountAPI;
    private final TokenRestAPI tokenAPI;
    private final TransactionMonitorAPI monitorAPI;

    @Inject
    public RestApiResource( BlockRestAPI blockRestAPI,  
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


