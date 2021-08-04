package com.syntifi.ori.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
 
@Provider
public class ORIExceptionHandler implements ExceptionMapper<ORIException> {

    @Override
    public Response toResponse(ORIException exception) 
    {
        return Response.status(exception.getStatus()).entity(exception.getMessage()).build();  
    }
}