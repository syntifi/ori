package com.syntifi.ori.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * ORI exception, containing the status code and message to be parsed in a Json
 * object that will
 * finally be returned to the user in a readeable form
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
public class ORIException extends WebApplicationException {

    private final Status status;

    public ORIException(String msg, Status httpStatus) {
        super(msg);
        this.status = httpStatus;
    }

    public ORIException(String msg, int httpStatusCode) {
        super(msg);
        this.status = Status.fromStatusCode(httpStatusCode);
    }

    public ORIException(String msg, Exception e) {
        super(msg, e);
        this.status = Status.INTERNAL_SERVER_ERROR;
    }

    public ORIException(String msg, Exception e, int httpStatusCode) {
        super(msg, e);
        this.status = Status.fromStatusCode(httpStatusCode);
    }

    public Status getStatus() {
        return status;
    }
}