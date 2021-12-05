package com.syntifi.ori;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;

import com.syntifi.ori.rest.BlockRestAPI;
import com.syntifi.ori.rest.TransactionMonitorAPI;
import com.syntifi.ori.rest.TransactionRestAPI;

@Singleton
public class RestApiResource {
    private final BlockRestAPI blockAPI;
    private final TransactionRestAPI transactionAPI;
    private final TransactionMonitorAPI monitorAPI;

    @Inject
    public RestApiResource( BlockRestAPI blockRestAPI,  
                TransactionRestAPI transactionRestAPI, 
                TransactionMonitorAPI transactionMonitorAPI) {
        this.blockAPI = blockRestAPI;
        this.transactionAPI = transactionRestAPI;
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

    @Path("/")
    public TransactionMonitorAPI getMonitorResource() {
        return monitorAPI;
    }   
}


