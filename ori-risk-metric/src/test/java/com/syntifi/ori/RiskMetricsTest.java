package com.syntifi.ori;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

import com.syntifi.ori.dto.TransferDTO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link RiskMetrics} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class RiskMetricsTest {

    @Test
    public void testcalculateScores() {
        Double[] amount = { 5E3, 9.5E3, 8E3, 9.36E3, 7E3, 5E3, 0.1E3, 9.234E3, 9.6E3, 8E3 };
        var now = OffsetDateTime.now();
        List<TransferDTO> in = new LinkedList<>();
        List<TransferDTO> out = new LinkedList<>();
        for (int i = 0; i < amount.length; i++) {
            var ini = new TransferDTO();
            ini.setAmount(amount[i]);
            ini.setFromHash("A");
            ini.setToHash("B");
            ini.setTokenSymbol("SYM");
            ini.setBlockHash("block");
            ini.setHash("in" + ("" + i));
            ini.setTimeStamp(now.minusDays(10L - i));
            in.add(ini);
            var outi = new TransferDTO();
            outi.setAmount(amount[i]);
            outi.setFromHash("A");
            outi.setToHash("B");
            outi.setTokenSymbol("SYM");
            outi.setBlockHash("block");
            outi.setHash("out" + ("" + i));
            outi.setTimeStamp(now.minusDays(10L - i).plusMinutes(1L));
            out.add(outi);
        }
        double threshold = 10E3;
        int minNumberOfTransactions = 10;
        int longWindow = 10;
        int shortWindow = 6;
        Assertions.assertEquals(0.4, RiskMetrics.calculateStructuringOverTimeScore(in, out, threshold, minNumberOfTransactions));
        Assertions.assertEquals(1.0, RiskMetrics.calculateFlowThroughScore(in, out, longWindow, minNumberOfTransactions));
        Assertions.assertEquals(38934.0/70794.0, RiskMetrics.calculateUnusualOutgoingVolumeScore(out, shortWindow, longWindow, minNumberOfTransactions));
        Assertions.assertEquals(0.4, RiskMetrics.calculateUnusualBehaviourScore(out, longWindow, minNumberOfTransactions));
    }

}
