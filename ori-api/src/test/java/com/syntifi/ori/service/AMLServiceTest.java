package com.syntifi.ori.service;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.model.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * {@link AMLService} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
public class AMLServiceTest {

    @Test
    public void testcalculateScores() {
        Chain chain = Chain.builder().name("chain").build();
        Token token = Token.builder()
                .symbol("ABC")
                .name("ABC")
                .chain(chain).build();
        Block block = Block.builder()
                .hash("block")
                .chain(chain)
                .era(0L)
                .height(0L)
                .root("root")
                .validator("validator")
                .timeStamp(OffsetDateTime.now())
                .parent(null).build();
        Account a = Account.builder().hash("A").build();
        Account b = Account.builder().hash("B").build();
        Double[] amount = { 5E3, 9.5E3, 8E3, 9.36E3, 7E3, 5E3, 0.1E3, 9.234E3, 9.6E3, 8E3 };
        var now = OffsetDateTime.now();
        List<Transfer> in = new LinkedList<>();
        List<Transfer> out = new LinkedList<>();
        for (int i = 0; i < amount.length; i++) {
            var ini = new Transfer();
            ini.setAmount(amount[i]);
            ini.setFromAccount(a);
            ini.setToAccount(b);
            ini.setToken(token);
            ini.setBlock(block);
            ini.setHash("in" + ("" + i));
            ini.setTimeStamp(now.minusDays(10L - i));
            in.add(ini);
            var outi = new Transfer();
            outi.setAmount(amount[i]);
            outi.setFromAccount(a);
            outi.setToAccount(b);
            outi.setToken(token);
            outi.setBlock(block);
            outi.setHash("out" + ("" + i));
            outi.setTimeStamp(now.minusDays(10L - i).plusMinutes(1L));
            out.add(outi);
        }
        AMLService amlService = new AMLService();
        var scores = amlService.calculateScores(in, out, 10E3, 6, 10);
        Assertions.assertNotNull(scores);
        Assertions.assertEquals(0.4, scores.getStructuringOverTimeScore());
        Assertions.assertEquals(1.0, scores.getFlowThroughScore());
        Assertions.assertEquals(38934.0 / 70794.0, scores.getUnusualOutgoingVolumeScore());
        Assertions.assertEquals(0.4, scores.getUnusualBehaviorScore());
    }

}
