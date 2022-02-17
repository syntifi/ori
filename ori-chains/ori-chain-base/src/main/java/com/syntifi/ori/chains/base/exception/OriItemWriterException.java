package com.syntifi.ori.chains.base.exception;

/**
 * Ori Exception for throwing on errors while writing items (api requests)
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class OriItemWriterException extends RuntimeException {
    public OriItemWriterException(String message, Throwable cause) {
        super(message, cause);
    }
}
