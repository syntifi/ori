package com.syntifi.ori.chains.base.service;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.syntifi.ori.chains.base.OriChainConfigProperties;
import com.syntifi.ori.chains.base.exception.OriChainCrawlerException;
import com.syntifi.ori.chains.base.model.MockChainBlock;
import com.syntifi.ori.chains.base.model.MockChainTransfer;

public class MockTestChainService {
    protected static final Random RANDOM = new Random();

    protected static final double MIN_TRANSFER_AMOUNT = 100.0;
    protected static final double MAX_TRANSFER_AMOUNT = 100000.0;

    protected static final long MIN_START_TIMESTAMP = OffsetDateTime.now().minusYears(3).toEpochSecond();
    protected static final long MAX_START_TIMESTAMP = OffsetDateTime.now().minusYears(1).toEpochSecond();

    protected static final short MIN_SECONDS_BETWEEN_BLOCKS = 1;
    protected static final short MAX_SECONDS_BETWEEN_BLOCKS = 360;

    protected static final int MIN_TRANSACTIONS = 0;
    protected static final int MAX_TRANSACTIONS = 100;

    protected static final int MAX_BLOCK_HEIGHT = 5;

    protected long lastTimestamp = 0;

    private int currentHeight = 0;

    private final List<MockChainBlock> blocks;

    private final List<MockChainTransfer> transfers;

    private final List<String> accountHashes;

    private OriChainConfigProperties oriChainConfigProperties;

    public MockTestChainService(OriChainConfigProperties oriChainConfigProperties) {
        this.blocks = new LinkedList<>();
        this.transfers = new LinkedList<>();
        this.accountHashes = new LinkedList<>();
        this.oriChainConfigProperties = oriChainConfigProperties;
        this.reset();
    }

    public void reset() {
        this.lastTimestamp = 0;
        this.currentHeight = 0;
        this.blocks.clear();
        this.transfers.clear();
        this.accountHashes.clear();

        long itemCount = oriChainConfigProperties.getChain().getBlockZeroHeight();

        while (itemCount < oriChainConfigProperties.getChain().getBlockZeroHeight() + MAX_BLOCK_HEIGHT) {
            MockChainBlock block = createBlock(itemCount);
            createTransfers(block.getHash(), randomInRange(MIN_TRANSACTIONS, MAX_TRANSACTIONS));
            itemCount++;
        }
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

    public MockChainBlock getBlock(long height) {
        if (height < this.blocks.size()) {
            return this.blocks.get((int) (height - oriChainConfigProperties.getChain().getBlockZeroHeight()));
        } else {
            return null;
        }
    }

    public MockChainBlock getNextBlock() {
        if (this.currentHeight < this.blocks.size()) {
            return this.blocks.get(this.currentHeight++);
        } else {
            return null;
        }
    }

    public List<MockChainTransfer> getTransfers(String blockHash) {
        return this.transfers.stream().filter(t -> t.getBlockHash().equals(blockHash)).collect(Collectors.toList());
    }

    private MockChainBlock createBlock(long itemCount) {
        long timestamp = lastTimestamp == 0 ? randomInRange(MIN_START_TIMESTAMP, MAX_START_TIMESTAMP)
                : lastTimestamp + randomInRange(MIN_SECONDS_BETWEEN_BLOCKS, MAX_SECONDS_BETWEEN_BLOCKS);

        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            throw new OriChainCrawlerException("error getting mock block", e);
        }

        MockChainBlock block = itemCount < (MAX_BLOCK_HEIGHT
                + oriChainConfigProperties.getChain().getBlockZeroHeight())
                        ? MockChainBlock.builder()
                                .hash(String.format(oriChainConfigProperties.getChain().getBlockZeroHash(),
                                        itemCount))
                                .parentHash(String.format(oriChainConfigProperties.getChain().getBlockZeroHash(),
                                        itemCount - 1))
                                .height(itemCount++)
                                .timestamp(timestamp)
                                .build()
                        : null;

        this.blocks.add(block);

        return block;
    }

    private List<MockChainTransfer> createTransfers(String blockHash, long transactionCount) {
        MockChainBlock block = getBlock(blockHash);
        List<MockChainTransfer> transfers = new LinkedList<>();
        for (int i = 0; i < transactionCount; i++) {
            String fromHash = getAccountHashFromPool(null);
            String toHash = getAccountHashFromPool(fromHash);
            transfers.add(MockChainTransfer.builder()
                    .hash("transaction-" + generateRandomHash(32))
                    .toHash(toHash)
                    .fromHash(fromHash)
                    .blockHash(blockHash)
                    .timestamp(block.getTimestamp())
                    .amount(randomInRange(MIN_TRANSFER_AMOUNT, MAX_TRANSFER_AMOUNT))
                    .build());
        }
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new OriChainCrawlerException("error getting mock block", e);
        }

        this.transfers.addAll(transfers);

        return transfers;
    }

    private String getAccountHashFromPool(String exceptHash) {
        int minAccounts = Math.max(this.transfers.size() / 4, 4);
        if (this.accountHashes.size() == 0 || this.accountHashes.size() < minAccounts) {
            while (this.accountHashes.size() <= minAccounts) {
                this.accountHashes.add("account-" + generateRandomHash(32));
            }
        }

        return this.accountHashes.stream().filter(h -> !h.equals(exceptHash)).collect(Collectors.toList())
                .get(RANDOM.nextInt(this.accountHashes.size() - 1));
    }

    private String generateRandomHash(int length) {
        StringBuffer sb = new StringBuffer();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(RANDOM.nextInt()));
        }

        return sb.toString().substring(0, length);
    }

    protected static long randomInRange(long min, long max) {
        return (long) (RANDOM.nextDouble() * (max - min)) + min;
    }

    protected static double randomInRange(double min, double max) {
        return (RANDOM.nextDouble() * (max - min)) + min;
    }
}
