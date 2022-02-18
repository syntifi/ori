package com.syntifi.ori.exception;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vertx.core.json.JsonObject;

/**
 * Json Handler used when finding issues in parsing Json parameters sent by the user 
 * in the body of a request 
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@Provider
public class ORIJsonProcessingExceptionHandler implements ExceptionMapper<JsonProcessingException> {
    
    @Override
    public Response toResponse(JsonProcessingException e) {

        return Response.status(Status.BAD_REQUEST).entity(
            new JsonObject().put("error", e.getOriginalMessage())).build();  

    }
}