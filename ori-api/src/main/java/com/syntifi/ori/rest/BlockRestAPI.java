package com.syntifi.ori.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.BlockRepository;
import com.syntifi.ori.repository.TokenRepository;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.panache.common.Sort;
import io.vertx.core.json.JsonObject;

/**
 * REST API block endpoints
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Block", description = "Block resources")
public class BlockRestAPI {
    @Inject
    BlockRepository blockRepository;

    @Inject
    TokenRepository tokenRepository;

    private void checkTokenSymbol(String symbol) {
        Token token = tokenRepository.findBySymbol(symbol);
        if (token == null) {
            throw new ORIException("Token not found", 404);
        }
    }

    /**
     * POST method to add and index a new block in ES
     * 
     * @param block
     * @return Response
     * @throws ORIException
     */
    @POST
    @Transactional
    @Path("/{tokenSymbol}/{parent}")
    public Response addBlock(Block child, @PathParam("tokenSymbol") String symbol, @PathParam("parent") String parent)
            throws ORIException {
        checkTokenSymbol(symbol);
        if (child.getHash() == null) {
            throw new ORIException("Block hash missing", 400);
        }
        Block parentBlock = blockRepository.findByHash(parent);
        if (parentBlock == null) {
            throw new ORIException("Parent block not found", 404);
        }
        try {
            child.setParent(parentBlock);
            blockRepository.persist(child);
        } catch (Exception e) {
            new ORIException(e.getMessage());
        }
        return Response.ok(new JsonObject().put("created", URI.create("/block/" + child.getHash()))).build();
    }

    /**
     * GET method to retreive all blocks indexed in ES. Note that the blocks are
     * sorted in reverse chronological order
     * 
     * @return List<Block>
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}")
    public List<Block> getAllBlocks(@PathParam("tokenSymbol") String symbol) throws ORIException {
        checkTokenSymbol(symbol);
        return blockRepository.listAll(Sort.descending("timeStamp"));
    }

    /**
     * GET method to retrieve a block by it's hash. Hash is given as a hex string.
     * 
     * @param hash
     * @return Block
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}/{hash}")
    public Block getBlockByHash(@PathParam("tokenSymbol") String symbol, @PathParam("hash") String hash)
            throws ORIException {
        checkTokenSymbol(symbol);
        return blockRepository.findByHash(hash);
    }

    /**
     * DELETE method to remove all blocks and clean the database
     * 
     * @return Response
     * @throws ORIException
     */
    @DELETE
    @Path("/{tokenSymbol}")
    public Response clear(@PathParam("tokenSymbol") String symbol) throws ORIException {
        checkTokenSymbol(symbol);
        try {
            long N = blockRepository.deleteAll();
            return Response.ok(new JsonObject()
                    .put("method", "DELETE")
                    .put("uri", "/block/" + symbol)
                    .put("N", N))
                    .build();
        } catch (Exception e) {
            throw new ORIException(e.getMessage());
        }
    }

    /**
     * DELETE method to remove a specific block given the hash. Hash is given as a
     * hex string
     * 
     * @param hash
     * @return
     * @throws ORIException
     */
    @DELETE
    @Path("/{tokenSymbol}/{hash}")
    public Response delete(@PathParam("tokenSymbol") String symbol, @PathParam("hash") String hash)
            throws ORIException {
        checkTokenSymbol(symbol);
        try {
            blockRepository.delete(blockRepository.findByHash(hash));
            return Response.ok(new JsonObject()
                    .put("method", "DELETE")
                    .put("uri", "/block/" + hash))
                    .build();
        } catch (Exception e) {
            throw new ORIException(e.getMessage());
        }
    }

}
