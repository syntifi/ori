package com.syntifi.ori.service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import com.syntifi.ori.dto.AMLRulesDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 * AML rules calculator
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
public class AMLService {

    private static int minNumberOfTransactions = ConfigProvider.getConfig()
            .getValue("ori.aml.min-number-transactions", int.class);

    public AMLRulesDTO calculateScores(List<Transaction> in, List<Transaction> out, Double threshold,
            Integer shortWindow, Integer longWindow)
            throws ORIException {
        sanityCheck(in, out);
        AMLRulesDTO scores = new AMLRulesDTO();

        scores.setFlowThroughScore(calculateFlowThroughScore(in, out, longWindow));
        scores.setStructuringOverTimeScore(calculateStructuringOverTimeScore(in, out, threshold));
        scores.setUnusualBehaviorScore(calculateUnusualBehaviourScore(out, shortWindow));
        scores.setUnusualOutgoingVolumeScore(calculateUnusualOutgoingVolumeScore(out, shortWindow, longWindow));
        return scores;
    }

    // TODO: Throw an ORIException in a service? Does it make sense?
    private void sanityCheck(List<Transaction> in, List<Transaction> out) throws ORIException {
        int n = (in == null ? 0 : in.size()) + (out == null ? 0 : out.size());
        if (n == 0) {
            throw new ORIException(
                    "Unable to calculate scores because the given account does not contain any transaction",
                    Status.NOT_FOUND.getStatusCode());
        }
    }

    // TODO: the reporting threshold is normally expressed in FIAT currency, a
    // conversion is needed here
    /***
     * This rule returns the proportion of transactions falling in the interval
     * [0.9, 1[
     * of the reporting threshold (ori.aml.reportting-threshold) over the historical
     * period (ori.aml.historical-length).
     * 
     * @return double between [0,1]
     */
    private double calculateStructuringOverTimeScore(List<Transaction> in, List<Transaction> out, Double thresh) {
        double threshold = thresh == null
                ? ConfigProvider.getConfig().getValue("ori.aml.reporting-threshold", double.class)
                : thresh;
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

    /***
     * This rule returns the percentual increase in outgoing transactions over a
     * short
     * window moving average (ori.aml.short-window). It is meant to spot an unusual
     * increase in outgoing transactions over the moving average window. The score
     * is
     * set to 0.0 if there was a decrease in outgoing transactions or if the number
     * of
     * outgoing transactions is low.
     * 
     * @return double between [0,1]
     */
    private double calculateUnusualOutgoingVolumeScore(List<Transaction> out, Integer shortWindow,
            Integer longWindow) {
        int sWindow = shortWindow == null
                ? ConfigProvider.getConfig().getValue("ori.aml.short-window", int.class)
                : shortWindow;

        int lWindow = longWindow == null
                ? ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class)
                : longWindow;

        OffsetDateTime lastDate = out.get(0).getTimeStamp();
        for (Transaction tx : out) {
            if (lastDate.isBefore(tx.getTimeStamp())) {
                lastDate = tx.getTimeStamp();
            }
        }

        OffsetDateTime[] dates = {
                lastDate.minusDays(lWindow),
                lastDate.minusDays(sWindow)
        };

        double amountOutShortWindow = 0;
        double amountOutLongWindow = 0;
        int nOutLongWindow = 0;
        for (Transaction transaction : out) {
            if (transaction.getTimeStamp().isAfter(dates[1])) {
                amountOutShortWindow = amountOutShortWindow + transaction.getAmount();
            }
            if (transaction.getTimeStamp().isAfter(dates[0])) {
                amountOutLongWindow = amountOutLongWindow + transaction.getAmount();
                nOutLongWindow = nOutLongWindow + 1;
            }
        }
        return nOutLongWindow < minNumberOfTransactions ? 0.0 : amountOutShortWindow * 1.0 / amountOutLongWindow;
    }

    /****
     * This rule returns how much does the last moving average transaction deviate
     * from
     * the average value of transactions.
     * 
     * @return double between [0,1]
     */
    private double calculateUnusualBehaviourScore(List<Transaction> out, Integer shortWindow) {
        int window = shortWindow == null
                ? ConfigProvider.getConfig().getValue("ori.aml.short-window", int.class)
                : shortWindow;
        OffsetDateTime fromDate = out.get(0).getTimeStamp().minusDays(window);
        List<Double> windowOut = out.stream()
                .filter(x -> x.getTimeStamp().isAfter(fromDate))
                .map(x -> x.getAmount())
                .collect(Collectors.toList());
        double windowAvg = windowOut.isEmpty()
                ? 0.0
                : windowOut.stream().reduce(0.0, (x, y) -> x + y)/windowOut.size();
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

    /****
     * This rule returns the ratio of total outgoing value and total incoming value
     * over
     * the window (ori.aml.mid-window). This is supposed to indicate "fake" accounts
     * used
     * to increase the difficulty of tracing the coin.
     * 
     * @return double between [0,1]
     */
    private double calculateFlowThroughScore(List<Transaction> in, List<Transaction> out, Integer midWindow) {
        int window = midWindow == null
                ? ConfigProvider.getConfig().getValue("ori.aml.mid-window", int.class)
                : midWindow;

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
