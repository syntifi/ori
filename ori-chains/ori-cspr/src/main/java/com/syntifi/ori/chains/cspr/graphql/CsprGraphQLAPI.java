package com.syntifi.ori.chains.cspr.graphql;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.QueryParam;

import com.syntifi.casper.Casper;
import com.syntifi.casper.model.chain.get.block.CasperBlock;
import com.syntifi.casper.model.chain.get.block.transfer.CasperTransfer;
import com.syntifi.casper.model.info.get.peers.CasperNode;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

/**
 * GraphQL Casper queries to interact with the Casper Network. This is a "fake" GraphQL, 
 * it uses the java-sdk behind the scenes to implicitly calls the RPC method and retrieve
 * the whole object. The filtering of the requested fields happens only afterwards, before
 * returning the results to the client.
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@GraphQLApi
public class CsprGraphQLAPI {

    Casper casperService = new Casper(
        ConfigProvider.getConfig().getValue("casper.node", String.class),
        ConfigProvider.getConfig().getValue("casper.port", int.class),
        ConfigProvider.getConfig().getValue("casper.timeout", int.class),
        ConfigProvider.getConfig().getValue("casper.threads", int.class));

    /**
     * Query to return a block from the Casper network given the hash 
     * 
     * @param blockHash
     * @return CasperBlock
     * @throws IOException
     * @throws InterruptedException
     */
    @Query
    @Description("Get Casper block information ")
    public CasperBlock getCasperBlockByHash(@QueryParam("blockHash") String blockHash) throws IOException, InterruptedException {
        return casperService.getBlockByHash(blockHash);
    }

    /**
     * Query to return a block from the Casper network given the height
     * 
     * @param blockHeight
     * @return CasperBlock
     * @throws IOException
     * @throws InterruptedException
     */
    @Query
    @Description("Get Casper block information ")
    public CasperBlock getCasperBlockByHeight(@QueryParam("blockHeight") long blockHeight) throws IOException, InterruptedException {
        return casperService.getBlockByHeight(blockHeight);
    }

    /**
     * Query to return all nodes active in the Casper Network
     * 
     * @return List<CasperNode>
     * @throws IOException
     * @throws InterruptedException
     */
    @Query
    @Description("Get Casper nodes")
    public List<CasperNode> getCasperNodes() throws IOException, InterruptedException {
        return casperService.getNodes();
    }

    /**
     * Query to retrieve all transactions in a given block given the block's hash 
     * 
     * @param blockHash
     * @return List<CasperTransfer>
     * @throws IOException
     * @throws InterruptedException
     */
    @Query
    @Description("Get block transfers")
    public List<CasperTransfer> getCasperTransfers(@QueryParam("blockHash") String blockHash) throws IOException, InterruptedException {
        return casperService.getTransfersByBlockHash(blockHash).transfers;
    }


}
