package com.syntifi.ori.task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import org.eclipse.microprofile.config.ConfigProvider;
import javax.inject.Inject;

import com.syntifi.casper.Casper;
import com.syntifi.casper.model.chain.get.block.CasperBlock;
import com.syntifi.casper.model.chain.get.block.transfer.CasperTransferData;
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.service.BlockService;
import com.syntifi.ori.service.TransactionService;


/**
 * Quartz job to crawl the Casper network. It uses the casper-sdk with the parameters 
 * in the application.propertios or environmental variables that specify the Casper 
 * nodes to query, the ammount of threads, the timeout and the RPC node port
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@ApplicationScoped 
public class CasperCrawlJob implements Job {
    int threads = ConfigProvider.getConfig().getValue("casper.threads", int.class);
    Casper casperService = new Casper(
        Arrays.asList(ConfigProvider.getConfig().getValue("casper.nodes", String.class).split(",")),
        ConfigProvider.getConfig().getValue("casper.port", int.class),
        ConfigProvider.getConfig().getValue("casper.timeout", int.class),
        ConfigProvider.getConfig().getValue("casper.threads", int.class));

    @Inject
    BlockService blockService;

    @Inject
    TransactionService transactionService;

    /**
     * Reads the last block available in the database and sends a query to retrieve the next 
     * couple of blocks (the number being the same as the threads size) in the casper network.
     * This methods uses the casper-sdk to communicate with the casper network and get a java object
     * as response. The needed fields are then indexed in the ES database.
     */
    public void execute(JobExecutionContext context) {
        long lastBlockHeight;
        long i;

        try {
            lastBlockHeight = casperService.getLastBlock().header.height;
        } catch (Exception e){
            lastBlockHeight = 0;
        }

        try {
            i = blockService.getLastBlock().height + 1 ;
        } catch (Exception e){
            i = 0;
        }
        
        List<Long> heights = new LinkedList<>();
        if (i < lastBlockHeight){
            try {
                for (long k=0; k<threads; k++) {
                    heights.add(i+k);
                }
                List<CasperBlock> blocks = casperService.getBlocksByBlockHeights(heights);
                List<CasperTransferData> transferss = casperService.getTransfersByBlockHeights(heights);
                for (CasperBlock block: blocks){
                    CasperTransferData transfers = transferss.stream()
                                .filter(x -> block.hash.equals(x.blockHash))
                                .findAny()
                                .orElse(null);
                    blockService.index(new Block(block.header.timeStamp, 
                                                    block.hash,
                                                    block.header.height,
                                                    block.header.eraId,
                                                    block.header.parentHash,
                                                    block.header.stateRootHash,
                                                    block.body.proposer));
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
                    heights.remove(block.header.height);
                }

            } catch (Exception e) {
            }
        }
    }
}
