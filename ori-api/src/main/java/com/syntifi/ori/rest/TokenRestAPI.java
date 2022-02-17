package com.syntifi.ori.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.Response.Status;

import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TokenMapper;
import com.syntifi.ori.model.Token;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.panache.common.Sort;
import io.vertx.core.json.JsonObject;

/**
 * Ori Rest Api for {@link Token} endpoints
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Token", description = "Token resources")
public class TokenRestAPI extends AbstractBaseRestApi {

    @POST
    @Transactional
    public Response addToken(TokenDTO tokenDTO) {
        Token token = TokenMapper.toModel(tokenDTO);

        boolean exists = tokenRepository.existsAlready(token.getSymbol());
        if (exists) {
            throw new ORIException(token.getSymbol() + " exists already", Status.CONFLICT);
        }

        tokenRepository.check(token);
        tokenRepository.persist(token);

        return Response.ok(new JsonObject().put("created",
                URI.create("/token/" + token.getSymbol())))
                .build();
    }

    @GET
    public List<TokenDTO> getAllTokens() {
        return tokenRepository.listAll(Sort.ascending("symbol")).stream().map(TokenMapper::fromModel)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{tokenSymbol}")
    public TokenDTO getTokenBySymbol(@PathParam("tokenSymbol") String symbol) {
        try {
            Token result = tokenRepository.findBySymbol(symbol);
            return TokenMapper.fromModel(result);
        } catch (NoResultException e) {
            throw new ORIException(symbol + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(symbol + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Transactional
    @Path("/{tokenSymbol}")
    public Response deleteToken(@PathParam("tokenSymbol") String symbol) {
        try {
            Token token = tokenRepository.findBySymbol(symbol);

            tokenRepository.delete(token);

            return Response.ok(new JsonObject()
                    .put("method", "DELETE")
                    .put("uri", "/token/" + symbol)).build();
        } catch (NoResultException e) {
            throw new ORIException(symbol + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(symbol + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }
}