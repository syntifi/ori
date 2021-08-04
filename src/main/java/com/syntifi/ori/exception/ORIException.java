package com.syntifi.ori.exception;

import java.io.Serializable;
import javax.ws.rs.core.Response.Status;

public class ORIException extends Exception implements Serializable {
    
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