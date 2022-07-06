package com.syntifi.ori.rest;

import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TokenMapper;
import com.syntifi.ori.model.Token;
import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ori Rest Api for {@link Token} endpoints
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Unremovable
@RegisterForReflection
@Tag(name = "Token", description = "Token resources")
public class TokenRestAPI extends AbstractBaseRestApi {

    /**
     * POST method to persist a new token in the DB
     *
     * @param chain
     * @param tokenDTO
     * @return
     */
    @POST
    @Transactional
    public Response addToken(@PathParam("chain") String chain, TokenDTO tokenDTO) {
        getChainOr404(chain);

        tokenDTO.setChainName(chain);

        Token token = TokenMapper.toModel(tokenDTO);

        boolean exists = tokenRepository.existsAlready(chain, token.getSymbol());
        if (exists) {
            throw new ORIException(token.getSymbol() + " exists already", Status.CONFLICT);
        }

        tokenRepository.check(token);
        tokenRepository.persist(token);

        return Response.ok(new JsonObject()
                        .put("method", "POST")
                        .put("uri", URI.create(String.format("/chain/%s/token/%s", chain, token.getSymbol()))))
                .build();
    }

    /**
     * GET method to return all tokens stored in the DB
     *
     * @return
     */
    @GET
    public List<TokenDTO> getAllTokens(@PathParam("chain") String chain) {
        return tokenRepository.findByChain(chain).stream().map(TokenMapper::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * GET method to return a token given its symbol
     *
     * @param chain
     * @param tokenSymbol
     * @return
     */
    @GET
    @Path("/{tokenSymbol}")
    public TokenDTO getTokenBySymbol(@PathParam("chain") String chain,
                                     @PathParam("tokenSymbol") String tokenSymbol) {
        try {
            Token result = tokenRepository.findByChainAndSymbol(chain, tokenSymbol);
            return TokenMapper.fromModel(result);
        } catch (NoResultException e) {
            throw new ORIException(tokenSymbol + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(tokenSymbol + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE method to drop a tiven token from the database
     *
     * @param chain
     * @param tokenSymbol
     * @return
     */
    @DELETE
    @Transactional
    @Path("/{tokenSymbol}")
    public Response deleteToken(@PathParam("chain") String chain, @PathParam("tokenSymbol") String tokenSymbol) {
        try {
            Token token = tokenRepository.findByChainAndSymbol(chain, tokenSymbol);

            tokenRepository.delete(token);

            return Response.ok(new JsonObject()
                            .put("method", "DELETE")
                            .put("uri", String.format("/chain/%s/token/%s", chain, tokenSymbol)))
                    .build();
        } catch (NoResultException e) {
            throw new ORIException(tokenSymbol + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(tokenSymbol + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }
}