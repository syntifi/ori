package com.syntifi.ori.chains.base.exception;

/**
 * Ori Exception for throwing on errors while crawling
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class OriChainCrawlerException extends RuntimeException {
    public OriChainCrawlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
