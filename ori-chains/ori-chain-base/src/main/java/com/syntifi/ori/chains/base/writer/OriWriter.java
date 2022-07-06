package com.syntifi.ori.chains.base.writer;

import java.util.List;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.exception.OriItemWriterException;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.dto.TransferDTO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * The default ORI data writer
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Component
public class OriWriter implements ItemWriter<OriData> {

    protected static final Log logger = LogFactory.getLog(OriWriter.class);

    /**
     * {@link OriClient} reference
     * 
     * @return the {@link OriClient} object
     */
    private OriClient oriClient;

    /**
     * {@link OriChainConfigProperties} reference
     * 
     * @return the {@link OriChainConfigProperties} object
     */
    private OriChainConfigProperties oriChainConfigProperties;

    /**
     * Constructs an instance of an ItemWriter for writing data to Ori which was
     * read from a blockchain
     * 
     * @param oriClient                the ori client to be used by this ItemWriter
     * @param oriChainConfigProperties the ori chain configuration
     */
    public OriWriter(OriClient oriClient, OriChainConfigProperties oriChainConfigProperties) {
        this.oriClient = oriClient;
        this.oriChainConfigProperties = oriChainConfigProperties;
    }

    /**
     * Process each object on the list and calls client methods to save data to ORI
     * database
     * 
     * @param oriDataList the resulting list of read and processed data
     */
    @Override
    public void write(List<? extends OriData> oriDataList) {
        // Write Block
        writeBlock(oriDataList);

        // Write transactions
        writeTransactions(oriDataList);
    }

    /**
     * Writes the block or blocks
     * 
     * @param oriDataList the resulting list of read and processed data
     */
    private void writeBlock(List<? extends OriData> oriDataList) {
        if (oriDataList == null || oriDataList.isEmpty()) {
            throw new OriItemWriterException("should not receive an empty list on OriWriter", null);
        } else if (oriDataList.size() > 1) {
            try {
                oriClient.postBlocks(oriChainConfigProperties.getChain().getTokenSymbol(),
                        oriDataList.stream().map(OriData::getBlock)
                                .collect(Collectors.toList()));
            } catch (WebClientResponseException e) {
                if (e.getStatusCode() != HttpStatus.CONFLICT) {
                    throw new OriItemWriterException(
                            String.format("error while writing blocks - (%s[%s])", e.getMessage(),
                                    getExceptionCause(e)),
                            e);
                } else {
                    logger.warn(
                            String.format("blocks (or some blocks) already exists (%s blocks)", oriDataList.size()));
                }
            }
        } else {
            OriData oriData = oriDataList.get(0);
            try {
                oriClient.postBlock(oriChainConfigProperties.getChain().getTokenSymbol(), oriData.getBlock());
            } catch (WebClientResponseException e) {
                if (e.getStatusCode() != HttpStatus.CONFLICT) {
                    throw new OriItemWriterException(String.format(
                            "error while writing block %s - (%s[%s])", oriData.getBlock().getHash(),
                            e.getMessage(), getExceptionCause(e)), e);
                } else {
                    logger.warn(String.format("block %s already exists", oriData.getBlock().getHash()));
                }
            }
        }
    }

    /**
     * Writes the transactions
     * 
     * @param oriDataList the resulting list of read and processed data
     */
    private void writeTransactions(List<? extends OriData> oriDataList) {
        for (OriData oriDataItem : oriDataList) {
            if (oriDataItem.getTransfers() != null) {
                for (TransferDTO transfer : oriDataItem.getTransfers()) {
                    writeAccount(transfer.getFromHash());
                    writeAccount(transfer.getToHash());
                    try {
                        oriClient.postTransfer(oriChainConfigProperties.getChain().getTokenSymbol(),
                                transfer);
                    } catch (WebClientResponseException e) {
                        if (e.getStatusCode() != HttpStatus.CONFLICT) {
                            throw new OriItemWriterException(String.format(
                                    "error while writing transaction %s: (%s[%s])", transfer.getHash(), e.getMessage(),
                                    getExceptionCause(e)), e);
                        } else {
                            logger.warn(String.format("transaction %s already exists", transfer.getHash()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Writes the accounts
     * 
     * @param hash the account hash to write
     */
    private void writeAccount(String hash) {
        if (hash == null) {
            return;
        }

        try {
            oriClient.postAccount(oriChainConfigProperties.getChain().getTokenSymbol(),
                    AccountDTO.builder().hash(hash).build());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() != HttpStatus.CONFLICT) {
                throw new OriItemWriterException(String.format(
                        "error while writing account %s - (%s[%s])", hash, e.getMessage(),
                        getExceptionCause(e)), e);
            } else {
                logger.warn(String.format("account %s already exists", hash));
            }
        }
    }

    /**
     * Helper method to extract the exception cause and throw info if/when neeeded
     * 
     * @param e the exception
     * @return the cause message or "no cause" if none found
     */
    private String getExceptionCause(WebClientResponseException e) {
        return e.getCause() != null ? e.getCause().getMessage() : "no cause";
    }
}
