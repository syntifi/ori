package com.syntifi.ori.graphql;

import java.io.IOException;
import java.util.List;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.graphql.Description;

import javax.ws.rs.QueryParam;

import com.syntifi.casper.model.info.get.peers.CasperNode;
import com.syntifi.casper.model.chain.get.block.CasperBlock;
import com.syntifi.casper.model.chain.get.block.transfer.CasperTransfer;
import com.syntifi.casper.Casper;


@GraphQLApi
public class CasperGraphQLAPI {

    Casper casperService = new Casper(
        ConfigProvider.getConfig().getValue("casper.node", String.class),
        ConfigProvider.getConfig().getValue("casper.port", int.class),
        ConfigProvider.getConfig().getValue("casper.timeout", int.class),
        ConfigProvider.getConfig().getValue("casper.threads", int.class));

    @Query
    @Description("Get Casper block information ")
    public CasperBlock getCasperBlockByHash(@QueryParam("blockHash") String blockHash) throws IOException, InterruptedException {
        return casperService.getBlockByHash(blockHash);
    }

    @Query
    @Description("Get Casper block information ")
    public CasperBlock getCasperBlockByHeight(@QueryParam("blockHeight") long blockHeight) throws IOException, InterruptedException {
        return casperService.getBlockByHeight(blockHeight);
    }
    @Query
    @Description("Get Casper nodes")
    public List<CasperNode> getCasperNodes() throws IOException, InterruptedException {
        return casperService.getNodes();
    }

    @Query
    @Description("Get block transfers")
    public List<CasperTransfer> getCasperTransfers(@QueryParam("blockHash") String blockHash) throws IOException, InterruptedException {
        return casperService.getTransfersByBlockHash(blockHash).transfers;
    }


}
