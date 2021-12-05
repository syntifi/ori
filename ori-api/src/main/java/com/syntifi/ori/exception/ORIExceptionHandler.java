package com.syntifi.ori.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.vertx.core.json.JsonObject;

/**
 * Handler that maps the ORIExcept to a jsonObject 
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@Provider
public class ORIExceptionHandler implements ExceptionMapper<ORIException> {

    @Override
    public Response toResponse(ORIException e) 
    {
        return Response.status(e.getStatus()).entity(
            new JsonObject().put("error", e.getMessage())).build();  
    }
}