package com.syntifi.ori.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import io.vertx.core.json.JsonObject;
 
@Provider
public class ORIExceptionHandler implements ExceptionMapper<ORIException> {

    @Override
    public Response toResponse(ORIException e) 
    {
        return Response.status(e.getStatus()).entity(
            new JsonObject().put("error", e.getMessage())).build();  
    }
}