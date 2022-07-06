package com.syntifi.ori.rest;

import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.inject.Singleton;
import javax.ws.rs.Path;

/**
 * Ori Rest Api resource endpoints for {@link BlockRestAPI},
 * {@link TransferRestAPI}, {@link AccountRestAPI}, {@link TokenRestAPI} and
 * {@link TransferMonitorAPI}
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Unremovable
@RegisterForReflection
public class RestApiResource {
    private final BlockRestAPI blockAPI;
    private final TransferRestAPI transferAPI;
    private final AccountRestAPI accountAPI;
    private final TokenRestAPI tokenAPI;
    private final ChainRestAPI chainAPI;
    private final TransferMonitorAPI monitorAPI;

    public RestApiResource(BlockRestAPI blockRestAPI,
                           TransferRestAPI transferRestAPI,
                           AccountRestAPI accountRestAPI,
                           TokenRestAPI tokenRestAPI,
                           ChainRestAPI chainAPI,
                           TransferMonitorAPI transferMonitorAPI) {
        this.blockAPI = blockRestAPI;
        this.transferAPI = transferRestAPI;
        this.accountAPI = accountRestAPI;
        this.tokenAPI = tokenRestAPI;
        this.chainAPI = chainAPI;
        this.monitorAPI = transferMonitorAPI;
    }

    @Path("/chain/{chain}/block")
    public BlockRestAPI getBlockResource() {
        return blockAPI;
    }

    @Path("/chain/{chain}/transfer")
    public TransferRestAPI getTransferResource() {
        return transferAPI;
    }

    @Path("/chain/{chain}/account")
    public AccountRestAPI getAccountResource() {
        return accountAPI;
    }

    @Path("/chain/{chain}/token")
    public TokenRestAPI getTokenResource() {
        return tokenAPI;
    }

    @Path("/chain")
    public ChainRestAPI getChainResource() {
        return chainAPI;
    }

    @Path("/chain/{chain}/monitor")
    public TransferMonitorAPI getMonitorResource() {
        return monitorAPI;
    }
}
