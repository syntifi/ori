package com.syntifi.ori.service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
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

    public AMLRulesDTO calculateScores(List<Transaction> in, List<Transaction> out) throws ORIException {
        sanityCheck(in, out);
        AMLRulesDTO scores = AMLRulesDTO.builder().build();

        scores.setFlowThroughScore(calculateFlowThroughScore(in, out));
        scores.setStructuringOverTimeScore(calculateStructuringOverTimeScore(in, out));
        scores.setUnusualBehaviorScore(calculateUnusualBehaviourScore(in, out));
        scores.setUnusualOutgoingVolumeScore(calculateUnusualOutgoingVolumeScore(in, out));

        return scores;
    }

    //TODO: Throw an ORIException in a service? Does it make sense?
    private void sanityCheck(List<Transaction> in, List<Transaction> out) throws ORIException {
        int n = (in == null ? 0 : in.size()) + (out == null ? 0 : out.size());
        if (n == 0) {
            throw new ORIException(
                    "Unable to calculate scores because the given account does not contain any transaction",
                    Status.NOT_FOUND.getStatusCode());
        }
    }

    /***
     * This rule returns the proportion of transactions falling in the interval
     * [0.9, 1[
     * of the reporting threshold (ori.aml.reportting-threshold) over the historical
     * period (ori.aml.historical-length).
     * 
     * @return double between [0,1]
     */
    private double calculateStructuringOverTimeScore(List<Transaction> in, List<Transaction> out) {
        double threshold = ConfigProvider.getConfig().getValue("ori.aml.reporting-threshold", double.class);
        double[] interval = { 0.9 * threshold, 1.0 * threshold };
        int n = in.size() + out.size();
        Long nIn = in.stream()
                .filter(x -> (x.getAmount() < interval[1]) && (x.getAmount() >= interval[0]))
                .collect(Collectors.counting());
        Long nOut = in.stream()
                .filter(x -> (x.getAmount() < interval[1]) && (x.getAmount() >= interval[0]))
                .collect(Collectors.counting());
        return (nOut + nIn) * 1.0 / n;
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
    private double calculateUnusualOutgoingVolumeScore(List<Transaction> in, List<Transaction> out) {
        if (out.size() < 10) {
            return 0.0;
        }
        int window = ConfigProvider.getConfig().getValue("ori.aml.short-window", int.class);

        OffsetDateTime lastDate = out.get(0).getTimeStamp().plusDays(1);

        // LocalDate lastDate = out.get(0).getTimeStamp()
        // .toInstant()
        // .atZone(ZoneId.of("GMT"))
        // .plusDays(1)
        // .toLocalDate();
        // Date[] dates = { java.sql.Date.valueOf(lastDate.minusDays(2L * window)),
        // java.sql.Date.valueOf(lastDate.minusDays(window)),
        // java.sql.Date.valueOf(lastDate) };
        OffsetDateTime[] dates = {
                lastDate.minusDays(2L * window),
                lastDate.minusDays(window),
                lastDate
        };

        int[] nOutWindow = { 0, 0 };
        for (Transaction transaction : out) {
            if ((transaction.getTimeStamp().isAfter(dates[0])) && (transaction.getTimeStamp().isBefore(dates[1]))) {
                nOutWindow[0] = nOutWindow[0] + 1;
            }
            if ((transaction.getTimeStamp().isAfter(dates[1])) && (transaction.getTimeStamp().isBefore(dates[2]))) {
                nOutWindow[1] = nOutWindow[1] + 1;
            }
        }
        return Math.atan(Math.max(nOutWindow[1] * 1.0 / nOutWindow[0], 0.0)) / Math.PI / 2.0;
    }

    /****
     * This rule returns how much does the last moving average transaction deviate
     * from
     * the average value of transactions.
     * 
     * @return double between [0,1]
     */
    private double calculateUnusualBehaviourScore(List<Transaction> in, List<Transaction> out) {
        int window = ConfigProvider.getConfig().getValue("ori.aml.short-window", int.class);
        // Date fromDate = java.sql.Date.valueOf(out.get(0).getTimeStamp().toInstant()
        // .atZone(ZoneId.of("GMT"))
        // .minusDays(window)
        // .toLocalDate());
        OffsetDateTime fromDate = out.get(0).getTimeStamp().minusDays(window);
        double windowAvg = out.stream()
                .filter(x -> x.getTimeStamp().isAfter(fromDate))
                .collect(Collectors.summingDouble(x -> x.getAmount()));
        double avg = out.stream()
                .collect(Collectors.summingDouble(x -> x.getAmount()));
        double std = out.stream()
                .collect(Collectors.summingDouble(x -> (x.getAmount() - avg) * (x.getAmount() - avg)))
                / (out.size() - 1);
        return Math.atan(Math.max((windowAvg - avg) / std, 0.0)) / Math.PI / 2.0;
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
    private double calculateFlowThroughScore(List<Transaction> in, List<Transaction> out) {
        int window = ConfigProvider.getConfig().getValue("ori.aml.mid-window", int.class);

        // Date lastDate = out.get(0).getTimeStamp().after(in.get(0).getTimeStamp())
        // ? out.get(0).getTimeStamp()
        // : in.get(0).getTimeStamp();
        // Date fromDate = java.sql.Date.valueOf(lastDate.toInstant()
        // .atZone(ZoneId.of("GMT"))
        // .minusDays(window)
        // .toLocalDate());

        OffsetDateTime lastDate = out.get(0).getTimeStamp().isAfter(in.get(0).getTimeStamp())
                ? out.get(0).getTimeStamp()
                : in.get(0).getTimeStamp();
        OffsetDateTime fromDate = lastDate.minus(window, ChronoUnit.DAYS);
        double inValue = in.stream()
                .filter(x -> x.getTimeStamp().isAfter(fromDate))
                .collect(Collectors.summingDouble(x -> x.getAmount()));
        double outValue = out.stream()
                .filter(x -> x.getTimeStamp().isAfter(fromDate))
                .collect(Collectors.summingDouble(x -> x.getAmount()));
        return Math.exp(-Math.pow(outValue / inValue, 2) / 0.01);
    }

}
