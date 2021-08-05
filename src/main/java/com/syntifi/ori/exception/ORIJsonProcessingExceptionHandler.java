package com.syntifi.ori.exception;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.json.JsonObject;

@Provider
public class ORIJsonProcessingExceptionHandler implements ExceptionMapper<JsonProcessingException> {
    
    @Override
    public Response toResponse(JsonProcessingException e) {

        return Response.status(Status.BAD_REQUEST).entity(
            new JsonObject().put("error", e.getOriginalMessage())).build();  

    }
}