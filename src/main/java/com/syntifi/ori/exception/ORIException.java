package com.syntifi.ori.exception;

import java.io.Serializable;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * ORI exception, containing the status code and message to be parsed in a Json object that will 
 * finally be returned to the user in a readeable form
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
public class ORIException extends WebApplicationException implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private Status status = Status.BAD_REQUEST;
 
    public ORIException() {
        super();
    }
    public ORIException(String msg)   {
        super(msg);
    }
    public ORIException(String msg, int httpStatusCode)   {
        super(msg);
        this.status = Status.fromStatusCode(httpStatusCode);
    }
    public ORIException(String msg, Exception e)  {
        super(msg, e);
    }
    public ORIException(String msg, Exception e, int httpStatusCode)  {
        super(msg, e);
        this.status = Status.fromStatusCode(httpStatusCode);
    }
    public Status getStatus(){
        return status;
    }
}