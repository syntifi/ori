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

@Path("/block")
@Tag(name = "Block", description = "Block resources")
public class BlockRestAPI {
    @Inject
    BlockService blockService;

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

    @GET
    public List<Block> getAllBlocks() throws ORIException {
        try {
            return blockService.getAllBlocks();
        } catch (Exception e) {
            throw blockService.parseElasticError(e);
        }
    }

    @GET
    @Path("/{hash}")
    public Block getBlockByHash(@PathParam("hash") String hash) throws ORIException {
        try {
            return blockService.getBlockByHash(hash);
        } catch (Exception e) {
            throw blockService.parseElasticError(e);
        }
    }

    @DELETE
    public Response clear() throws ORIException {
        try {
            return Response.ok(blockService.clear().getRequestLine()).build();
        } catch (Exception e) {
            throw blockService.parseElasticError(e);
        }
    }

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
