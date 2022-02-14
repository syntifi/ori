package com.syntifi.ori.chains.base.client;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.syntifi.ori.chains.base.exception.OriChainCrawlerException;
import com.syntifi.ori.chains.base.model.MockChainBlock;
import com.syntifi.ori.chains.base.model.MockChainTransfer;

public class MockTestChainService extends TestChainService<MockChainBlock, MockChainTransfer> {

    public MockChainBlock getBlock() {
        long timestamp = lastTimestamp == 0 ? randomInRange(MIN_START_TIMESTAMP, MAX_START_TIMESTAMP)
                : lastTimestamp + randomInRange(MIN_SECONDS_BETWEEN_BLOCKS, MAX_SECONDS_BETWEEN_BLOCKS);

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new OriChainCrawlerException("error getting mock block", e);
        }

        return currentHeight < MAX_BLOCK_HEIGHT ? MockChainBlock.builder()
                .hash(String.format("BLOCK-HASH-BLOCK-HEIGHT-%s", currentHeight))
                .parentHash(String.format("BLOCK-HASH-BLOCK-HEIGHT-%s", currentHeight - 1))
                .height(currentHeight++)
                .timestamp(timestamp)
                .build() : null;
    }

    public List<MockChainTransfer> getTransfers(String blockHash) {
        List<MockChainTransfer> transfers = new LinkedList<>();
        for (int i = 0; i < randomInRange(MIN_TRANSACTIONS, MAX_TRANSACTIONS); i++) {
            transfers.add(MockChainTransfer.builder()
                    .hash("hash")
                    .toHash("toHash")
                    .fromHash("fromHash")
                    .blockHash(blockHash)
                    .amount(randomInRange(MIN_TRANSFER_AMOUNT, MAX_TRANSFER_AMOUNT))
                    .build());
        }
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new OriChainCrawlerException("error getting mock block", e);
        }
        return transfers;
    }
}
