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

import com.syntifi.ori.dto.TokenDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Token;
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
@Tag(name = "Token", description = "Token resources")
public class TokenRestAPI {
    @Inject
    TokenRepository tokenRepository;

    @POST
    @Transactional
    public Response addToken(Token token) {
        boolean exists = tokenRepository.existsAlready(token);
        if (exists) {
            throw new ORIException(token.getSymbol() + " exists already", 400);
        }
        tokenRepository.check(token);
        tokenRepository.persist(token);
        return Response.ok(new JsonObject().put("created",
                URI.create("/token/" + token.getSymbol())))
                .build();
    }

    @GET
    public List<TokenDTO> getAllTokens() {
        return tokenRepository.listAll(Sort.ascending("symbol")).stream().map(TokenDTO::fromModel)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{tokenSymbol}")
    public TokenDTO getTokenBySymbol(@PathParam("tokenSymbol") String symbol) {
        Token result = tokenRepository.findBySymbol(symbol);
        if (result == null) {
            throw new ORIException(symbol + " not found", 404);
        }
        return TokenDTO.fromModel(result);
    }

    @DELETE
    @Transactional
    @Path("/{tokenSymbol}")
    public Response deleteToken(@PathParam("tokenSymbol") String symbol) {
        Token token = tokenRepository.findBySymbol(symbol);
        if (token != null) {
            tokenRepository.delete(token);
        } else {
            throw new ORIException(symbol + " not found", 404);
        }
        return Response.ok(new JsonObject()
                .put("method", "DELETE")
                .put("uri", "/token/" + symbol))
                .build();
    }
}