package com.syntifi.ori.chains.base.listeners;

import com.syntifi.ori.chains.base.model.ChainData;
import com.syntifi.ori.chains.base.model.OriData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.core.annotation.OnProcessError;

/**
 * Chain ProcessListener for listening to batch process events
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class ChainItemProcessListener<T extends ChainData<?, ?>> {

    protected static final Log logger = LogFactory.getLog(ChainItemProcessListener.class);

    @BeforeProcess
    public void beforeProcess(T item) {
        logger.info(String.format("Starting processing of block with %s transfers.",
                item.getChainTransfers() != null ? item.getChainTransfers().size() : 0));
    }

    @AfterProcess
    public void afterProcess(T item, OriData result) {
        logger.info(String.format("Finished processing of block with %s transfers.",
                result.getTransfers() != null ? result.getTransfers().size() : 0));
    }

    @OnProcessError
    public void onProcessError(T item, Exception e) {
        logger.error(String.format("Error processing block: %s", e.getMessage()));
    }
}