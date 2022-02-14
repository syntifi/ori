package com.syntifi.ori.chains.base.client;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.exception.OriChainCrawlerException;
import com.syntifi.ori.chains.base.model.MockChainBlock;
import com.syntifi.ori.chains.base.model.MockChainTransfer;

import org.springframework.beans.factory.annotation.Autowired;

public class MockTestChainService {
    protected static final long MIN_TRANSFER_AMOUNT = 100;
    protected static final long MAX_TRANSFER_AMOUNT = 100000000;

    protected static final long MIN_START_TIMESTAMP = OffsetDateTime.now().minusYears(3).toEpochSecond();
    protected static final long MAX_START_TIMESTAMP = OffsetDateTime.now().minusYears(1).toEpochSecond();

    protected static final short MIN_SECONDS_BETWEEN_BLOCKS = 1;
    protected static final short MAX_SECONDS_BETWEEN_BLOCKS = 360;

    protected static final int MIN_TRANSACTIONS = 0;
    protected static final int MAX_TRANSACTIONS = 100;

    protected static final int MAX_BLOCK_HEIGHT = 5;

    protected long currentHeight = 0;
    protected long lastTimestamp = 0;

    public List<MockChainBlock> blocks;

    public List<MockChainTransfer> transfers;

    public MockTestChainService(OriChainConfigProperties oriChainConfigProperties) {
        this.blocks = new LinkedList<>();
        this.transfers = new LinkedList<>();
        this.currentHeight = oriChainConfigProperties.getChainBlockZeroHeight() + 1L;
    }

    public List<MockChainBlock> getAllBlocks() {
        return this.blocks;
    }

    public List<MockChainTransfer> getAllTransfers() {
        return this.transfers;
    }

    public MockChainBlock getBlock(String hash) {
        Optional<MockChainBlock> block = this.blocks.stream().filter(b -> b.getHash().equals(hash)).findFirst();
        return block.isPresent() ? block.get() : null;
    }

    public MockChainTransfer getTransfer(String hash) {
        Optional<MockChainTransfer> transfer = this.transfers.stream().filter(b -> b.getHash().equals(hash))
                .findFirst();
        return transfer.isPresent() ? transfer.get() : null;
    }

    public MockChainBlock getBlock() {
        long timestamp = lastTimestamp == 0 ? randomInRange(MIN_START_TIMESTAMP, MAX_START_TIMESTAMP)
                : lastTimestamp + randomInRange(MIN_SECONDS_BETWEEN_BLOCKS, MAX_SECONDS_BETWEEN_BLOCKS);

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new OriChainCrawlerException("error getting mock block", e);
        }

        MockChainBlock block = currentHeight < MAX_BLOCK_HEIGHT ? MockChainBlock.builder()
                .hash(String.format("BLOCK-HASH-BLOCK-HEIGHT-%s", currentHeight))
                .parentHash(String.format("BLOCK-HASH-BLOCK-HEIGHT-%s", currentHeight - 1))
                .height(currentHeight++)
                .timestamp(timestamp)
                .build() : null;

        this.blocks.add(block);

        return block;
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

        this.transfers.addAll(transfers);

        return transfers;
    }

    protected static long randomInRange(long min, long max) {
        return (long) (Math.random() * (max - min)) + min;
    }
}
