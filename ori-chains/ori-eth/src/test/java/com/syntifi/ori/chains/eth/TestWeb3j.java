package com.syntifi.ori.chains.eth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.core.config.plugins.convert.HexConverter;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionObject;
import org.web3j.protocol.http.HttpService;

public class TestWeb3j {

    private static Web3j eth;

    @BeforeAll
    public static void setUpConnection() {
        eth = Web3j.build(new HttpService("http://localhost:8545"));
    }

    /*
     * @Test
     * public void testGetBlock() throws IOException {
     * BigInteger height = BigInteger.valueOf(2405787L);
     * var blockParam = DefaultBlockParameter.valueOf(height);
     * EthBlock block = eth.ethGetBlockByNumber(blockParam, true).send();
     * List<TransactionResult> txs = block.getResult().getTransactions();
     * TransactionObject tx = (TransactionObject) txs.get(0).get();
     * assertEquals("0x0c0e3eaa5e980433ba4ce2e971c0637cdf754195", tx.getFrom());
     * assertEquals("0xbfc39b6f805a9e40e77291aff27aee3c96915bdd", tx.getTo());
     * assertEquals(2711277800000000000L, tx.getValue().longValue());
     * assertEquals(
     * "0x121465ee0770324df725e6b4c7081d89d5dde69614fa48312c4208d1efcdc67d",
     * tx.getHash());
     * String hash = tx.getHash();
     * assert(txs.size()>0);
     * assertNotNull(block);
     * }
     */
}
