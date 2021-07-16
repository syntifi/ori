package com.syntifi.ori.rest;

import java.io.IOException;
import java.util.List;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.PathParam;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.syntifi.ori.model.Block;
import com.syntifi.ori.service.BlockService;

import javax.inject.Inject;

@Path("/block")
@Tag(name = "Block", description = "Block resources")
public class BlockRestAPI {
    @Inject
    BlockService blockService;

    @POST
    public Response index(Block block) throws IOException {
        if (block.hash == null) {
            throw new BadRequestException("Block hash missing");
        }
        blockService.index(block);
        return Response.created(URI.create("/block/" + block.hash)).build();
    }

    @GET
    public List<Block> getAllBlocks() throws IOException {
        return blockService.getAllBlocks();
    }

    @DELETE
    public Response clear() throws IOException {
        return Response.ok(blockService.clear().toString()).build();
    }

    @DELETE
    public Response delete(String hash) throws IOException {
        return Response.ok(blockService.delete(hash).toString()).build();
    }

    @GET
    @Path("/{hash}")
    public Block getBlockByHash(@PathParam("hash") String hash) throws IOException {
        return blockService.getBlockByHash(hash);
    }
}
