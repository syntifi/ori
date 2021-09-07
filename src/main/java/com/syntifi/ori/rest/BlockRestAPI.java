package com.syntifi.ori.rest;

import java.util.List;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.vertx.core.json.JsonObject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.service.BlockService;

import javax.inject.Inject;

/**
 * REST API block endpoints 
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@Path("/block")
@Tag(name = "Block", description = "Block resources")
public class BlockRestAPI {
    @Inject
    BlockService blockService;
    
    /**
     * POST method to add and index a new block in ES
     *  
     * @param block
     * @return Response
     * @throws ORIException
     */
    @POST
    public Response index(Block block) throws ORIException {
        if (block.hash == null) {
            throw new ORIException("Block hash missing", 404);
        }
        try {
            blockService.index(block);
        } catch (Exception e) {
            throw blockService.parseElasticError(e);
        }
        return Response.ok(new JsonObject().put("created", URI.create("/block/" + block.hash))).build();
    }

    /**
     * GET method to retreive all blocks indexed in ES. Note that the blocks are sorted in reverse 
     * chronological order
     * 
     * @return List<Block>
     * @throws ORIException
     */
    @GET
    public List<Block> getAllBlocks() throws ORIException {
        try {
            return blockService.getAllBlocks();
        } catch (Exception e) {
            throw blockService.parseElasticError(e);
        }
    }

    /**
     * GET method to retrieve a block by it's hash. Hash is given as a hex string.
     * 
     * @param hash
     * @return Block
     * @throws ORIException
     */
    @GET
    @Path("/{hash}")
    public Block getBlockByHash(@PathParam("hash") String hash) throws ORIException {
        try {
            return blockService.getBlockByHash(hash);
        } catch (Exception e) {
            throw blockService.parseElasticError(e);
        }
    }

    /**
     * DELETE method to remove all blocks and clean the database
     * 
     * @return Response
     * @throws ORIException
     */
    @DELETE
    public Response clear() throws ORIException {
        try {
            blockService.clear();
            return Response.ok(new JsonObject()
                                        .put("method", "DELETE")
                                        .put("uri", "/block"))
                            .build();
        } catch (Exception e) {
            throw blockService.parseElasticError(e);
        }
    }

    /**
     *  DELETE method to remove a specific block given the hash. Hash is given as a hex string
     * 
     * @param hash
     * @return
     * @throws ORIException
     */
    @DELETE
    @Path("/{hash}")
    public Response delete(@PathParam("hash") String hash) throws ORIException {
        try {
            blockService.delete(hash);
            return Response.ok(new JsonObject()
                                        .put("method", "DELETE")
                                        .put("uri", "/block/" + hash))
                            .build();
        } catch (Exception e) {
            throw blockService.parseElasticError(e);
        }
    }

}
