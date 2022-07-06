package com.syntifi.ori.rest;

import com.syntifi.ori.dto.BlockDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.BlockMapper;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.repository.BlockRepository;
import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ori Rest Api for {@link Block} endpoints
 * TODO: pagination
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 **/
@Singleton
@Unremovable
@RegisterForReflection
@Tag(name = "Block", description = "Block resources")
public class BlockRestAPI extends AbstractBaseRestApi {

    @Inject
    BlockRepository blockRepository;

    private void checkParent(String symbol, BlockDTO blockDTO, List<BlockDTO> blockDTOs) {
        boolean isFirstBlock = !blockRepository.existsAnyByChain(symbol);
        if (!isFirstBlock) {
            if (blockDTOs != null && blockDTOs.stream().anyMatch(t -> t.getHash().equals(blockDTO.getParent()))) {
                return;
            }
            try {
                blockRepository.findByChainNameAndHash(symbol, blockDTO.getParent());
            } catch (NoResultException e) {
                throw new ORIException("Parent block not found", Status.BAD_REQUEST);
            } catch (NonUniqueResultException e) {
                throw new ORIException("Parent block not unique", Status.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * POST method to persist a new block in the DB
     *
     * @param chain
     * @param blockDTO
     * @return
     * @throws ORIException
     **/
    @POST
    @Transactional
    public Response addBlock(@PathParam("chain") String chain,
                             BlockDTO blockDTO) throws ORIException {

        getChainOr404(chain);

        boolean exists = blockRepository.existsAlready(chain, blockDTO.getHash());
        if (exists) {
            throw new ORIException(blockDTO.getHash() + " exists already", Status.CONFLICT);
        }

        blockDTO.setChainName(chain);

        checkParent(chain, blockDTO, null);

        Block block = BlockMapper.toModel(blockDTO);

        blockRepository.check(block);
        blockRepository.persist(block);

        return Response.ok(new JsonObject()
                        .put("method", "POST")
                        .put("uri", URI.create(String.format("/chain/%s/block/%s", chain, block.getHash()))))
                .build();
    }

    /**
     * POST method to pesist a list many blocks to the DB at once
     *
     * @param chain
     * @param blockDTOs
     * @return
     * @throws ORIException
     */
    @POST
    @Transactional
    @Path("/multiple")
    public Response addBlocks(@PathParam("chain") String chain,
                              List<BlockDTO> blockDTOs) throws ORIException {

        getChainOr404(chain);

        ResponseBuilder response = new ResponseBuilderImpl().status(Status.CREATED);
        for (BlockDTO blockDTO : blockDTOs) {
            blockDTO.setChainName(chain);

            checkParent(chain, blockDTO, blockDTOs);

            Block block = BlockMapper.toModel(blockDTO);

            /*
             * TODO: WIP what to do if an entry exists?
             * When restarting crawler for instance, the whole last block should run again
             * to guarantee all accounts and transfers were also processed.
             */
            boolean exists = blockRepository.existsAlready(chain, blockDTO.getHash());
            if (exists) {
                // throw new ORIException(blockDTO.getHash() + " exists already",
                // Status.CONFLICT);
            } else {
                blockRepository.check(block);
                blockRepository.persist(block);
                response.link(URI.create(String.format("/chain/%s/block/%s", chain, block.getHash())), "self");
            }
        }

        return response.build();
    }

    /**
     * GET method to retrieve all blocks for a given chain name
     *
     * @param chain
     * @return
     * @throws ORIException
     */
    @GET
    public List<BlockDTO> getAllBlocks(@PathParam("chain") String chain) throws ORIException {

        getChainOr404(chain);

        return blockRepository.getBlocks(chain).stream().map(BlockMapper::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * GET method to retrieve the last block (chronologically speaking) for a given
     * chain chain
     *
     * @param chain
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/last")
    public BlockDTO getLastBlock(@PathParam("chain") String chain) throws ORIException {

        getChainOr404(chain);

        Block lastBlock = blockRepository.getLastBlock(chain);

        if (lastBlock == null) {
            throw new ORIException("Parent block not found", Status.NOT_FOUND);
        }

        return BlockMapper.fromModel(lastBlock);
    }

    /**
     * GET method to retrieve a block by it's hash. Hash is given as a hex string.
     *
     * @param chain
     * @param hash
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/{hash}")
    public BlockDTO getBlockByHash(@PathParam("chain") String chain, @PathParam("hash") String hash)
            throws ORIException {

        getChainOr404(chain);

        try {
            Block result = blockRepository.findByChainNameAndHash(chain, hash);
            return BlockMapper.fromModel(result);
        } catch (NoResultException e) {
            throw new ORIException(hash + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE method to remove a specific block stored in a given DB given its hash.
     * Hash is given as a hex string
     *
     * @param chain
     * @param hash
     * @return
     * @throws ORIException
     */
    @DELETE
    @Transactional
    @Path("/{hash}")
    public Response deleteBlock(@PathParam("chain") String chain, @PathParam("hash") String hash)
            throws ORIException {

        getChainOr404(chain);

        try {
            Block block = blockRepository.findByChainNameAndHash(chain, hash);
            if (block.getChain().getName().equals(chain)) {
                blockRepository.delete(block);
            } else {
                throw new ORIException("Forbidden", 403);
            }

            return Response.ok(new JsonObject()
                            .put("method", "DELETE")
                            .put("uri", String.format("/chain/%s/block/%s", chain, hash)))
                    .build();
        } catch (NoResultException e) {
            throw new ORIException(hash + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }
}
