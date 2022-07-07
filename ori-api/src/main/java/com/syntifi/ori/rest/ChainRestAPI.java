package com.syntifi.ori.rest;


import com.syntifi.ori.dto.ChainDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.ChainMapper;
import com.syntifi.ori.mapper.TokenMapper;
import com.syntifi.ori.model.Chain;
import io.quarkus.arc.Unremovable;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ori Rest Api for {@link Chain} endpoints
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.2.0
 */
@Singleton
@Unremovable
@RegisterForReflection
@Tag(name = "Chain", description = "Chain resources")
public class ChainRestAPI extends AbstractBaseRestApi {

    /**
     * POST method to persist a new chain in the DB
     *
     * @param chainDTO
     * @return http response
     */
    @POST
    @Transactional
    public Response addChain(ChainDTO chainDTO) {
        Chain chain = ChainMapper.toModel(chainDTO);

        boolean exists = chainRepository.existsAlready(chain.getName());
        if (exists) {
            throw new ORIException(chain.getName() + " exists already", Response.Status.CONFLICT);
        }

        chainRepository.check(chain);
        chainRepository.persist(chain);

        return Response.ok(new JsonObject()
                        .put("method", "POST")
                        .put("uri", URI.create("/chain/" + chain.getName())))
                .build();
    }

    /**
     * GET method to return all chains stored in the DB
     *
     * @return list of ChainDTOs
     */
    @GET
    public List<ChainDTO> getAllChains(@DefaultValue("0") @QueryParam("page") int page,
                                       @DefaultValue("25") @QueryParam("pagesSize") int pageSize) throws ORIException {

        return chainRepository.getAllChains()
                .page(Page.of(page, pageSize))
                .list()
                .stream()
                .map(ChainMapper::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * GET method to return a chain given its chain
     *
     * @param chain
     * @return ChainDTO
     */
    @GET
    @Path("/{chain}")
    public ChainDTO getChainByName(@PathParam("chain") String chain) {
        try {
            Chain result = chainRepository.findByName(chain);
            return ChainMapper.fromModel(result);
        } catch (NoResultException e) {
            throw new ORIException(chain + " not found", Response.Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(chain + " not unique", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE method to drop a given chain from the database
     *
     * @param chain
     * @return http response
     */
    @DELETE
    @Transactional
    @Path("/{chain}")
    public Response deleteChain(@PathParam("chain") String chain) {
        try {
            Chain _chain = chainRepository.findByName(chain);

            chainRepository.delete(_chain);

            return Response.ok(new JsonObject()
                    .put("method", "DELETE")
                    .put("uri", "/chain/" + chain)).build();
        } catch (NoResultException e) {
            throw new ORIException(chain + " not found", Response.Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(chain + " not unique", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
