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
import com.syntifi.ori.mapper.BlockMapper;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.repository.BlockRepository;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.vertx.core.json.JsonObject;

/**
 * REST API block endpoints
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Block", description = "Block resources")
public class BlockRestAPI extends AbstractBaseRestApi {

    @Inject
    BlockRepository blockRepository;

    /**
     * POST method to add and index a new block in ES
     * 
     * @param symbol
     * @param parent
     * @param blockDTO
     * @return Response
     * @throws ORIException
     */
    @POST
    @Transactional
    @Path("/{tokenSymbol}/parent/{parent}")
    public Response addBlock(@PathParam("tokenSymbol") String symbol, @PathParam("parent") String parent,
            BlockDTO blockDTO) throws ORIException {

        Token token = getTokenOr404(symbol);

        boolean exists = blockRepository.existsAlready(symbol, blockDTO.getHash());
        if (exists) {
            throw new ORIException(blockDTO.getHash() + " exists already", 400);
        }

        blockDTO.setTokenSymbol(symbol);
        blockDTO.setParent(parent);

        Block child = BlockMapper.toModel(blockDTO, tokenRepository);
        child.setToken(token);

        boolean isFirstBlock = blockRepository.getBlocks(symbol).isEmpty();
        Block parentBlock = null;
        if (!isFirstBlock) {
            parentBlock = blockRepository.findByHash(symbol, parent);
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
     * @param symbol
     * @return a list of {@link BlockDTO}
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}")
    public List<BlockDTO> getAllBlocks(@PathParam("tokenSymbol") String symbol) throws ORIException {
        getTokenOr404(symbol);

        return blockRepository.getBlocks(symbol).stream().map(BlockMapper::fromModel)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{tokenSymbol}/last")
    public BlockDTO getLastBlock(@PathParam("tokenSymbol") String symbol) throws ORIException {
        getTokenOr404(symbol);

        return BlockMapper.fromModel(blockRepository.getLastBlock(symbol));
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
        getTokenOr404(symbol);

        Block result = blockRepository.findByHash(symbol, hash);
        if (result == null) {
            throw new ORIException(hash + " not found", 404);
        }

        return BlockMapper.fromModel(result);
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
        getTokenOr404(symbol);

        Block block = blockRepository.findByHash(symbol, hash);
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
