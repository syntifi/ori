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

    @Test
    void readInput() throws IOException {
        DefaultBlockParameter blockParam = DefaultBlockParameter.valueOf(BigInteger.valueOf(100490L));
        String input = "606060405260405161014938038061014983398101604052805160805160a051919092019190808383815160019081018155600090600160a060020a0333169060029060038390559183525061010260205260408220555b82518110156100e257828181518110156100025790602001906020020151600160a060020a03166002600050826002016101008110156100025790900160005081905550806002016101026000506000858481518110156100025790602001906020020151600160a060020a0316815260200190815260200160002060005081905550600101610057565b81600060005081905550505050806101056000508190555061010662015180420490565b6101075550505050602d8061011c6000396000f3003660008037602060003660003473d658a4b8247c14868f3c512fa5cbb6e458e4a98961235a5a03f260206000f30000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000003635c9adc5dea000000000000000000000000000000000000000000000000000000000000000000005000000000000000000000000d268fb48fa174088a25a120aff0fd8eb0c2d4c87000000000000000000000000f91fb5529a5371da8c9dac3d9d4244a5a642c556000000000000000000000000f698016399d4868faa45924d98b4a202424cdb5c000000000000000000000000b15e28e6e96ae767223559fc575051059f0d4b51000000000000000000000000a3d940115503b0fd10494424c1b57d2a26e7d169";
        EthBlock block = eth.ethGetBlockByNumber(blockParam, true).send();

        TransactionObject to = ((TransactionObject) block.getBlock().getTransactions().iterator().next().get());

        assertEquals(BigInteger.valueOf(100490L), block.getBlock().getNumber());

        byte[] test = Hex.decode(input);

        ByteBuffer buffer = ByteBuffer.allocate(test.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        for (byte b : test) buffer.put(b);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        System.out.println(new String(buffer.array(), "ASCII"));

    }
}
