package com.syntifi.ori.exception;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.annotation.Priority;
import javax.json.stream.JsonParsingException;
import io.vertx.core.json.JsonObject;

/**
 * Json Handler used when finding issues in parsing Json parameters sent by the user 
 * in the body of a request 
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@Provider
@Priority(1)
public class ORIJsonParsingExceptionHandler implements ExceptionMapper<JsonParsingException> {
    
    @Override
    public Response toResponse(JsonParsingException e) {

        return Response.status(Status.BAD_REQUEST).entity(
            new JsonObject().put("error", e.getLocalizedMessage())).build();  

    }
}