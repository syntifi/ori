package com.syntifi.ori.task;

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

import org.jboss.logging.Logger;

@ApplicationScoped 
public class CasperCrawlJob implements Job {
    private static final Logger LOG = Logger.getLogger(CasperCrawlJob.class);
    private static final Casper casperService = new Casper(
        ConfigProvider.getConfig().getValue("casper.node", String.class),
        ConfigProvider.getConfig().getValue("casper.port", int.class),
        ConfigProvider.getConfig().getValue("casper.timeout", int.class));

    @Inject
    BlockService blockService;

    @Inject
    TransactionService transactionService;

    public void execute(JobExecutionContext context) {
        LOG.info("========= Crawl job called ==========");
        long lastBlockHeight;
        long i;

        try {
            lastBlockHeight = casperService.getBlock().header.height;
        } catch (Exception e){
            lastBlockHeight = 0;
        }

        try {
            i = blockService.getLastBlock().height + 1 ;
        } catch (Exception e){
            i = 0;
        }

        if (i < lastBlockHeight){
            try {
                var block = casperService.getBlock(i);
                blockService.index(new Block(block.header.timeStamp, 
                                                block.hash,
                                                block.header.height,
                                                block.header.eraId,
                                                block.header.parentHash,
                                                block.header.stateRootHash,
                                                block.body.proposer));
                var transfers = casperService.getTransfers(i);
                List<Transaction> transactions = transfers.stream()
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
            } catch (Exception e){
                LOG.info("========= Exception in CrawlJob ==========");
                LOG.info(e.getMessage());
            }
        }
    }
}
