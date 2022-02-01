package com.syntifi.ori.chains.base.writer;

import java.util.List;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.exception.OriItemWriterException;
import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;
import com.syntifi.ori.client.OriRestClient;
import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * The default ORI block and transfer writer
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertolace <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
public class OriBlockAndTransfersWriter implements ItemWriter<OriBlockAndTransfers> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OriBlockAndTransfersWriter.class);

    private String tokenSymbol;
    private OriRestClient oriRestClient;

    public OriBlockAndTransfersWriter(OriRestClient oriClient, String token) {
        oriRestClient = oriClient;
        tokenSymbol = token;
    }

    @Override
    public void write(List<? extends OriBlockAndTransfers> blockAndTransfersResults) {
        // Write Block
        writeBlock(blockAndTransfersResults);

        // Write transactions
        writeTransactions(blockAndTransfersResults);
    }

    private void writeBlock(List<? extends OriBlockAndTransfers> blockAndTransfersResults) {
        if (blockAndTransfersResults.size() > 1) {
            try {
                oriRestClient.postBlocks(tokenSymbol,
                        blockAndTransfersResults.stream().map(OriBlockAndTransfers::getBlock)
                                .collect(Collectors.toList()));
            } catch (WebClientResponseException e) {
                if (e.getStatusCode() != HttpStatus.CONFLICT) {
                    throw new OriItemWriterException(String.format(
                            "error while writing blocks (or blocks) - (% blocks)", blockAndTransfersResults.size()),
                            e);
                } else {
                    throw new OriItemWriterException(String.format(
                            "blocks (or some blocks) already exists (% blocks)", blockAndTransfersResults.size()),
                            e);
                }
            }
        } else {
            OriBlockAndTransfers oriBlockAndTransfers = blockAndTransfersResults.get(0);
            try {
                oriRestClient.postBlock(tokenSymbol, oriBlockAndTransfers.getBlock());
            } catch (WebClientResponseException e) {
                if (e.getStatusCode() != HttpStatus.CONFLICT) {
                    throw new OriItemWriterException(String.format(
                            "error while writing block %s", oriBlockAndTransfers.getBlock().getHash()), e);
                } else {
                    throw new OriItemWriterException(String.format(
                            "block %s already exists", oriBlockAndTransfers.getBlock().getHash()),
                            e);
                }
            }
        }
    }

    private void writeTransactions(List<? extends OriBlockAndTransfers> blockAndTransfersResults) {
        for (OriBlockAndTransfers oriBlockAndTransfers : blockAndTransfersResults) {
            if (oriBlockAndTransfers.getTransfers() != null) {
                for (TransactionDTO transfer : oriBlockAndTransfers.getTransfers()) {
                    writeAccount(transfer.getFromHash());
                    writeAccount(transfer.getToHash());
                    try {
                        oriRestClient.postTransfer(tokenSymbol, transfer);
                    } catch (WebClientResponseException e) {
                        if (e.getStatusCode() != HttpStatus.CONFLICT) {
                            throw new OriItemWriterException(String.format(
                                    "error while writing transaction %s", transfer.getHash()), e);
                        } else {
                            throw new OriItemWriterException(String.format(
                                    "transaction %s already exists", transfer.getHash()), e);
                        }
                    }
                }
            }
        }
    }

    private void writeAccount(String hash) {
        if (hash == null)
            return;

        try {
            oriRestClient.postAccount(tokenSymbol,
                    AccountDTO.builder().hash(hash).build());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() != HttpStatus.CONFLICT) {
                throw new OriItemWriterException(String.format(
                        "error while writing account %s", hash), e);
            } else {
                LOGGER.warn("account {} already exists", hash);
            }
        }
    }
}
