package com.syntifi.ori.chains.base.client;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.syntifi.ori.chains.base.exception.OriChainCrawlerException;
import com.syntifi.ori.chains.base.model.MockChainBlock;
import com.syntifi.ori.chains.base.model.MockChainTransfer;

public class MockChainService {
    private static final long MIN_TRANSFER_AMOUNT = 100;
    private static final long MAX_TRANSFER_AMOUNT = 100000000;

    private static final long MIN_START_TIMESTAMP = OffsetDateTime.now().minusYears(3).toEpochSecond();
    private static final long MAX_START_TIMESTAMP = OffsetDateTime.now().minusYears(1).toEpochSecond();

    private static final short MIN_SECONDS_BETWEEN_BLOCKS = 1;
    private static final short MAX_SECONDS_BETWEEN_BLOCKS = 360;

    private static final int MIN_TRANSACTIONS = 0;
    private static final int MAX_TRANSACTIONS = 100;

    private static final int MAX_BLOCK_HEIGHT = 5;

    private int currentHeight = 0;
    private long lastTimestamp = 0;

    public MockChainBlock getMockBlock() {
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

    public List<MockChainTransfer> getMockTransfers() {
        List<MockChainTransfer> transfers = new LinkedList<>();
        for (int i = 0; i < randomInRange(MIN_TRANSACTIONS, MAX_TRANSACTIONS); i++) {
            transfers.add(MockChainTransfer.builder()
                    .hash("hash")
                    .toHash("toHash")
                    .fromHash("fromHash")
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

    private static long randomInRange(long min, long max) {
        return (long) (Math.random() * (max - min)) + min;
    }
}
