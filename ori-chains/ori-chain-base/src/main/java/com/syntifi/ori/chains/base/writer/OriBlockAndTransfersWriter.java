package com.syntifi.ori.chains.base.writer;

import java.util.List;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.model.OriBlockAndTransfers;
import com.syntifi.ori.client.OriRestClient;
import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.dto.TransactionDTO;

import org.springframework.batch.item.ItemWriter;

/**
 * The default ORI block and transfer writer
 * 
 * @author Alexandre Carvalho <adcarvalho@gmail.com>
 * @author Andre Bertolace <andre@syntifi.com>
 * 
 * @since 0.1.0
 */
public class OriBlockAndTransfersWriter implements ItemWriter<OriBlockAndTransfers> {

    private String tokenSymbol;
    private OriRestClient oriRestClient;

    public OriBlockAndTransfersWriter(OriRestClient oriClient, String token) {
        oriRestClient = oriClient;
        tokenSymbol = token;
    }

    @Override
    public void write(List<? extends OriBlockAndTransfers> blockAndTransfersResults) {
        // Write Block
        if (blockAndTransfersResults.size() > 1) {
            oriRestClient.postBlocks(tokenSymbol,
                    blockAndTransfersResults.stream().map(OriBlockAndTransfers::getBlock).collect(Collectors.toList()));
        } else {
            OriBlockAndTransfers oriBlockAndTransfers = blockAndTransfersResults.get(0);
            oriRestClient.postBlock(tokenSymbol, oriBlockAndTransfers.getBlock());
        }

        // Write transactions
        for (OriBlockAndTransfers oriBlockAndTransfers : blockAndTransfersResults) {
            List<TransactionDTO> transfers = oriBlockAndTransfers.getTransfers();
            if (transfers != null) {
                for (TransactionDTO transfer : transfers) {
                    // TODO: PubKey/label needed from account?
                    oriRestClient.postAccount(tokenSymbol, AccountDTO.builder().hash(transfer.getFromHash()).build());
                    oriRestClient.postAccount(tokenSymbol, AccountDTO.builder().hash(transfer.getToHash()).build());
                    oriRestClient.postTransfer(tokenSymbol, transfer);
                }
            }
        }
    }
}
