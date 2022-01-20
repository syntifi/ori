package com.syntifi.ori.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.syntifi.ori.dto.BlockDTO;
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

    private Token getToken(String symbol) {
        Token token = tokenRepository.findBySymbol(symbol);
        if (token == null) {
            throw new ORIException("Token not found", 404);
        }
        return token;
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
    @Path("/{tokenSymbol}/parent/{parent}")
    public Response addBlock(Block child, @PathParam("tokenSymbol") String symbol, @PathParam("parent") String parent)
            throws ORIException {
        boolean exists = blockRepository.existsAlready(child);
        if (exists) {
            throw new ORIException(child.getHash() + " exists already", 400);
        }
        var token = symbol == null ? null : getToken(symbol);
        child.setToken(token);
        boolean isFirstBlock = blockRepository.count() == 0;
        Block parentBlock = null;
        if (!isFirstBlock) {
            parentBlock = blockRepository.findByHash(parent);
            if (parentBlock == null) {
                throw new ORIException("Parent block not found", 404);
            }
        }
        child.setParent(parentBlock);
        blockRepository.check(child);
        blockRepository.persist(child);
        return Response.ok(new JsonObject().put("created", URI.create("/block/" + symbol + "/hash/" + child.getHash())))
                .build();
    }

    /**
     * GET method to retrieve all blocks indexed in ES. Note that the blocks are
     * sorted in reverse chronological order
     * 
     * @return List<Block>
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}")
    public List<BlockDTO> getAllBlocks(@PathParam("tokenSymbol") String symbol) throws ORIException {
        getToken(symbol);
        return blockRepository.listAll(Sort.descending("timeStamp")).stream().map(BlockDTO::fromModel)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{tokenSymbol}/last")
    public BlockDTO getLastBlock(@PathParam("tokenSymbol") String symbol) throws ORIException {
        Token token = getToken(symbol);
        return BlockDTO.fromModel(blockRepository.getLastBlock(token));
    }

    /**
     * GET method to retrieve a block by it's hash. Hash is given as a hex string.
     * 
     * @param hash
     * @return Block
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}/hash/{hash}")
    public BlockDTO getBlockByHash(@PathParam("tokenSymbol") String symbol, @PathParam("hash") String hash)
            throws ORIException {
        getToken(symbol);
        Block result = blockRepository.findByHash(hash);
        if (result == null) {
            throw new ORIException(hash + " not found", 404);
        }
        return BlockDTO.fromModel(result);
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
    @Transactional
    @Path("/{tokenSymbol}/hash/{hash}")
    public Response deleteBlock(@PathParam("tokenSymbol") String symbol, @PathParam("hash") String hash)
            throws ORIException {
        Block block = blockRepository.findByHash(hash);
        if (block == null) {
            throw new ORIException(hash + " not found", 404);
        }
        if (block.getToken().getSymbol().equals(symbol)) {
            blockRepository.delete(block);
        } else {
            throw new ORIException("Forbidden", 403);
        }
        return Response.ok(new JsonObject()
                .put("method", "DELETE")
                .put("uri", "/block/" + symbol + "/hash/" + hash))
                .build();
    }

}
