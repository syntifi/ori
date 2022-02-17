package com.syntifi.ori.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.BlockMapper;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.repository.BlockRepository;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import io.vertx.core.json.JsonObject;

/**
 * Ori Rest Api for {@link Block} endpoints
 * TODO: pagination
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Block", description = "Block resources")
public class BlockRestAPI extends AbstractBaseRestApi {

    @Inject
    BlockRepository blockRepository;

    private void checkParent(String symbol, BlockDTO blockDTO, List<BlockDTO> blockDTOs) {
        boolean isFirstBlock = !blockRepository.existsAnyByToken(symbol);
        if (!isFirstBlock) {
            if (blockDTOs != null && blockDTOs.stream().anyMatch(t -> t.getHash().equals(blockDTO.getParent()))) {
                return;
            }
            try {
                blockRepository.findByTokenSymbolAndHash(symbol, blockDTO.getParent());
            } catch (NoResultException e) {
                throw new ORIException("Parent block not found", Status.BAD_REQUEST);
            } catch (NonUniqueResultException e) {
                throw new ORIException("Parent block not unique", Status.INTERNAL_SERVER_ERROR);
            }
        }
    }

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
    @Path("/{tokenSymbol}")
    public Response addBlock(@PathParam("tokenSymbol") String symbol,
            BlockDTO blockDTO) throws ORIException {

        getTokenOr404(symbol);

        boolean exists = blockRepository.existsAlready(symbol, blockDTO.getHash());
        if (exists) {
            throw new ORIException(blockDTO.getHash() + " exists already", Status.CONFLICT);
        }

        blockDTO.setTokenSymbol(symbol);

        checkParent(symbol, blockDTO, null);

        Block block = BlockMapper.toModel(blockDTO);

        blockRepository.check(block);
        blockRepository.persist(block);

        return Response.created(URI.create(String.format("/block/%s/hash/%s", symbol, block.getHash()))).build();
    }

    @POST
    @Transactional
    @Path("/{tokenSymbol}/multiple")
    public Response addBlocks(@PathParam("tokenSymbol") String symbol,
            List<BlockDTO> blockDTOs) throws ORIException {

        getTokenOr404(symbol);

        ResponseBuilder response = new ResponseBuilderImpl().status(Status.CREATED);
        for (BlockDTO blockDTO : blockDTOs) {
            blockDTO.setTokenSymbol(symbol);

            checkParent(symbol, blockDTO, blockDTOs);

            Block block = BlockMapper.toModel(blockDTO);

            /*
             * TODO: WIP what to do if an entry exists?
             * When restarting crawler for instance, the whole last block should run again
             * to guarantee all accounts and transactions were also processed.
             */
            boolean exists = blockRepository.existsAlready(symbol, blockDTO.getHash());
            if (exists) {
                // throw new ORIException(blockDTO.getHash() + " exists already",
                // Status.CONFLICT);
            } else {
                blockRepository.check(block);
                blockRepository.persist(block);
                response.link(URI.create(String.format("/block/%s/hash/%s", symbol, block.getHash())), "self");
            }
        }

        return response.build();
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

        Block lastBlock = blockRepository.getLastBlock(symbol);

        if (lastBlock == null) {
            throw new ORIException("Parent block not found", Status.NOT_FOUND);
        }

        return BlockMapper.fromModel(lastBlock);
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

        try {
            Block result = blockRepository.findByTokenSymbolAndHash(symbol, hash);
            return BlockMapper.fromModel(result);
        } catch (NoResultException e) {
            throw new ORIException(hash + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
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
    @Transactional
    @Path("/{tokenSymbol}/hash/{hash}")
    public Response deleteBlock(@PathParam("tokenSymbol") String symbol, @PathParam("hash") String hash)
            throws ORIException {
        getTokenOr404(symbol);

        try {
            Block block = blockRepository.findByTokenSymbolAndHash(symbol, hash);
            if (block.getToken().getSymbol().equals(symbol)) {
                blockRepository.delete(block);
            } else {
                throw new ORIException("Forbidden", 403);
            }

            return Response.ok(new JsonObject()
                    .put("method", "DELETE")
                    .put("uri", "/block/" + symbol + "/hash/" + hash))
                    .build();
        } catch (NoResultException e) {
            throw new ORIException(hash + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }
}
