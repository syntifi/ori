package com.syntifi.ori.chains.base.client;

import java.time.OffsetDateTime;
import java.util.List;

public abstract class TestChainService<B, T> {
    protected static final long MIN_TRANSFER_AMOUNT = 100;
    protected static final long MAX_TRANSFER_AMOUNT = 100000000;

    protected static final long MIN_START_TIMESTAMP = OffsetDateTime.now().minusYears(3).toEpochSecond();
    protected static final long MAX_START_TIMESTAMP = OffsetDateTime.now().minusYears(1).toEpochSecond();

    protected static final short MIN_SECONDS_BETWEEN_BLOCKS = 1;
    protected static final short MAX_SECONDS_BETWEEN_BLOCKS = 360;

    protected static final int MIN_TRANSACTIONS = 0;
    protected static final int MAX_TRANSACTIONS = 100;

    protected static final int MAX_BLOCK_HEIGHT = 5;

    protected int currentHeight = 0;
    protected long lastTimestamp = 0;

    protected static long randomInRange(long min, long max) {
        return (long) (Math.random() * (max - min)) + min;
    }

    public abstract B getBlock();

    public abstract List<T> getTransfers(String blockHash);
}
