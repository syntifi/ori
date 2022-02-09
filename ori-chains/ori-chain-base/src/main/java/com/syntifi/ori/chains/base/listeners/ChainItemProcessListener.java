package com.syntifi.ori.chains.base.listeners;

import com.syntifi.ori.chains.base.model.ChainData;
import com.syntifi.ori.chains.base.model.OriData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.core.annotation.OnProcessError;

public class ChainItemProcessListener<T extends ChainData<?, ?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChainItemProcessListener.class);

    @BeforeProcess
    public void beforeProcess(T item) {
        LOGGER.debug("Starting processing of block with {} transfers.",
                item.getChainTransfers() != null ? item.getChainTransfers().size() : 0);
    }

    @AfterProcess
    public void afterProcess(T item, OriData result) {
        LOGGER.debug("Finished processing of block with {} transfers.",
                result.getTransfers() != null ? result.getTransfers().size() : 0);
    }

    @OnProcessError
    public void onProcessError(T item, Exception e) {
        LOGGER.error("Error processing block: {}", e.getMessage());
    }
}