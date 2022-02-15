package com.syntifi.ori.chains.base.writer;

import java.util.List;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.exception.OriItemWriterException;
import com.syntifi.ori.chains.base.model.OriData;
import com.syntifi.ori.client.OriClient;
import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * The default ORI data writer
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertolace <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
@Component
public class OriWriter implements ItemWriter<OriData> {

    protected static final Log logger = LogFactory.getLog(OriWriter.class);

    private OriClient oriClient;
    private OriChainConfigProperties oriChainConfigProperties;

    public OriWriter(OriClient oriClient, OriChainConfigProperties oriChainConfigProperties) {
        this.oriClient = oriClient;
        this.oriChainConfigProperties = oriChainConfigProperties;
    }

    @Override
    public void write(List<? extends OriData> oriDataList) {
        // Write Block
        writeBlock(oriDataList);

        // Write transactions
        writeTransactions(oriDataList);
    }

    private void writeBlock(List<? extends OriData> oriDataList) {
        if (oriDataList == null || oriDataList.isEmpty()) {
            throw new OriItemWriterException("should not receive an empty list on OriWriter", null);
        } else if (oriDataList.size() > 1) {
            try {
                oriClient.postBlocks(oriChainConfigProperties.getChainTokenSymbol(),
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
                oriClient.postBlock(oriChainConfigProperties.getChainTokenSymbol(), oriData.getBlock());
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

    private void writeTransactions(List<? extends OriData> oriDataList) {
        for (OriData oriDataItem : oriDataList) {
            if (oriDataItem.getTransfers() != null) {
                for (TransactionDTO transfer : oriDataItem.getTransfers()) {
                    writeAccount(transfer.getFromHash());
                    writeAccount(transfer.getToHash());
                    try {
                        oriClient.postTransfer(oriChainConfigProperties.getChainTokenSymbol(), transfer);
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

    private void writeAccount(String hash) {
        if (hash == null) {
            return;
        }

        try {
            oriClient.postAccount(oriChainConfigProperties.getChainTokenSymbol(),
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

    private Object getExceptionCause(WebClientResponseException e) {
        return e.getCause() != null ? e.getCause().getMessage() : "no cause";
    }
}
