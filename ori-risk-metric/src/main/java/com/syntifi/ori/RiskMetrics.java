package com.syntifi.ori;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.syntifi.ori.dto.TransactionDTO;

/**
 * Ori Risk Metric calculator
 *
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
public class RiskMetrics {

    private RiskMetrics() {
    }

    // TODO: the reporting threshold is normally expressed in FIAT currency, a
    // conversion is needed here
    /**
     * This rule returns the proportion of transactions falling in the interval
     * [0.9, 1[ of the reporting threshold
     * 
     * @param in
     * @param out
     * @param threshold
     * @param minNumberOfTransactions
     * @return
     */
    public static double calculateStructuringOverTimeScore(List<TransactionDTO> in, List<TransactionDTO> out,
            Double threshold, int minNumberOfTransactions) {
        double[] interval = { 0.9 * threshold, 1.0 * threshold };
        int n = in.size() + out.size();
        Long nIn = in.stream()
                .filter(x -> (x.getAmount() < interval[1]) && (x.getAmount() >= interval[0]))
                .collect(Collectors.counting());
        Long nOut = in.stream()
                .filter(x -> (x.getAmount() < interval[1]) && (x.getAmount() >= interval[0]))
                .collect(Collectors.counting());
        return n < minNumberOfTransactions ? 0.0 : (nOut + nIn) * 1.0 / n;
    }

    /**
     * This rule returns the percentual increase in outgoing transactions over a
     * short window moving average. It is meant to spot an unusual increase in
     * outgoing transactions over the moving average window.
     * 
     * @param out
     * @param shortWindow
     * @param longWindow
     * @param minNumberOfTransactions
     * @return
     */
    public static double calculateUnusualOutgoingVolumeScore(List<TransactionDTO> out, Integer shortWindow,
            Integer longWindow, int minNumberOfTransactions) {

        OffsetDateTime lastDate = out.get(0).getTimeStamp();
        for (TransactionDTO tx : out) {
            if (lastDate.isBefore(tx.getTimeStamp())) {
                lastDate = tx.getTimeStamp();
            }
        }

        OffsetDateTime[] dates = {
                lastDate.minusDays(longWindow),
                lastDate.minusDays(shortWindow)
        };

        double amountOutShortWindow = 0;
        double amountOutLongWindow = 0;
        int nOutLongWindow = 0;
        for (TransactionDTO transaction : out) {
            if (transaction.getTimeStamp().isAfter(dates[1])) {
                amountOutShortWindow = amountOutShortWindow + transaction.getAmount();
            }
            if (transaction.getTimeStamp().isAfter(dates[0])) {
                amountOutLongWindow = amountOutLongWindow + transaction.getAmount();
                nOutLongWindow = nOutLongWindow + 1;
            }
        }
        return nOutLongWindow < minNumberOfTransactions ? 0.0
                : amountOutShortWindow * 1.0 / amountOutLongWindow;
    }

    /**
     * This rule returns how much does the last moving average transaction deviate
     * from the average value of transactions.
     * 
     * @param out
     * @param window
     * @param minNumberOfTransactions
     * @return
     */
    public static double calculateUnusualBehaviourScore(List<TransactionDTO> out, Integer window,
            int minNumberOfTransactions) {
        OffsetDateTime fromDate = out.get(0).getTimeStamp().minusDays(window);
        List<Double> windowOut = out.stream()
                .filter(x -> x.getTimeStamp().isAfter(fromDate))
                .map(x -> x.getAmount())
                .collect(Collectors.toList());
        double windowAvg = windowOut.isEmpty()
                ? 0.0
                : windowOut.stream().reduce(0.0, (x, y) -> x + y) / windowOut.size();
        List<Double> ammount = out.stream()
                .map(x -> x.getAmount())
                .collect(Collectors.toList());
        Collections.sort(ammount);
        int idx = 0;
        int N = ammount.size();
        while (idx < N && ammount.get(idx) < windowAvg) {
            idx = idx + 1;
        }
        return N < minNumberOfTransactions ? 0.0 : idx * 1.0 / N;
    }

    /**
     * This rule returns the ratio of total outgoing value and total incoming value
     * over the window. This is supposed to spot "fake" accounts used to increase
     * the difficulty of tracing the coin.
     * 
     * @param in
     * @param out
     * @param window
     * @param minNumberOfTransactions
     * @return
     */
    public static double calculateFlowThroughScore(List<TransactionDTO> in, List<TransactionDTO> out,
            Integer window, int minNumberOfTransactions) {
        OffsetDateTime lastDate = out.get(0).getTimeStamp().isAfter(in.get(0).getTimeStamp())
                ? out.get(0).getTimeStamp()
                : in.get(0).getTimeStamp();
        OffsetDateTime fromDate = lastDate.minusDays(window);
        List<Double> inValues = in.stream()
                .filter(x -> x.getTimeStamp().isAfter(fromDate))
                .map(x -> x.getAmount())
                .collect(Collectors.toList());
        List<Double> outValues = out.stream()
                .filter(x -> x.getTimeStamp().isAfter(fromDate))
                .map(x -> x.getAmount())
                .collect(Collectors.toList());
        int N = inValues.size() + outValues.size();
        Double outValue = outValues.stream()
                .reduce(0.0, (x, y) -> x + y);
        Double inValue = inValues.stream()
                .reduce(0.0, (x, y) -> x + y);
        return (N < minNumberOfTransactions || inValue == 0.0)
                ? 0.0
                : Math.max(outValue / inValue, 1.0);
    }

}
