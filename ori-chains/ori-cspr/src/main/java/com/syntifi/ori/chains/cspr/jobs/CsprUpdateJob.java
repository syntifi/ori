package com.syntifi.ori.chains.cspr.jobs;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import org.eclipse.microprofile.config.ConfigProvider;
import javax.inject.Inject;

import com.syntifi.casper.Casper;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.BlockService;
import com.syntifi.ori.service.TransactionService;

/**
 * Quartz job to update the blocks from the Casper network. It uses the casper-sdk 
 * with the parameters in the application.propertios or environmental variables that 
 * specify the Casper nodes to query, the ammount of threads, the timeout and the RPC node port
 * NOTE: This is meant to be used once the Casper network has been crawled and there are just
 * a couple of new blocks to be indexed
 *  
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@ApplicationScoped 
public class CsprUpdateJob implements Job {

    Casper casperService = new Casper(
        ConfigProvider.getConfig().getValue("casper.node", String.class),
        ConfigProvider.getConfig().getValue("casper.port", int.class),
        ConfigProvider.getConfig().getValue("casper.timeout", int.class),
        ConfigProvider.getConfig().getValue("casper.threads", int.class));

    @Inject
    BlockService blockService;

    @Inject
    TransactionService transactionService;    
   
    /**
     * Reads the last block available in the database and the last one in the casper network. 
     * It keeps requesting the casper nodes for the next block until the last block in the database
     * and in the network match.
     */
    public void execute(JobExecutionContext context) {

        long lastLocalBlockHeight;

        try {
            lastLocalBlockHeight = blockService.getLastBlock().height ;
        } catch (Exception e){
            lastLocalBlockHeight = Long.MAX_VALUE;
        }

        try {
            var lastBlock = casperService.getLastBlock();
            for (long i=lastLocalBlockHeight; i<=lastBlock.header.height; i++) {
                var block = casperService.getBlockByHeight(i);
                blockService.index(new Block(block.header.timeStamp, 
                                                block.hash,
                                                block.header.height,
                                                block.header.eraId,
                                                block.header.parentHash,
                                                block.header.stateRootHash,
                                                block.body.proposer));
                var transfers = casperService.getTransfersByBlockHeight(i);
                List<Transaction> transactions = transfers.transfers.stream()
                                    .map(transfer -> new Transaction(block.header.timeStamp,
                                                            transfer.deployHash,
                                                            transfer.from,
                                                            transfer.to,
                                                            transfer.amount,
                                                            block.hash))
                                    .collect(Collectors.toList());
                for (Transaction transaction : transactions) {
                    transactionService.index(transaction);
                }
            }
        } catch (Exception e){
        }
    }
}