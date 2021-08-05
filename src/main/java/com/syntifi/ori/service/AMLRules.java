package com.syntifi.ori.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.syntifi.ori.model.Transaction;

import org.eclipse.microprofile.config.ConfigProvider;

public class AMLRules {

    @JsonAlias("StructuringOverTimeScore")
    public double structuringOverTimeScore;

    @JsonAlias("UnusualOutgoingVolumeScore")
    public double unusualOutgoingVolumeScore;

    @JsonAlias("UnusualBehaviorScore")
    public double unusualBehaviorScore;

    @JsonAlias("FlowThroughScore")
    public double flowThroughScore;

    @JsonIgnore
    private List<Transaction> in;
    
    @JsonIgnore
    private List<Transaction> out;

    public AMLRules(List<Transaction> in, List<Transaction> out) {
        this.in = in;
        this.out = out;
    }

    public void calculateScores() {
        this.structuringOverTimeScore = this.calculateStructuringOverTimeScore();
        this.unusualOutgoingVolumeScore = this.calculateUnusualOutgoingVolumeScore();
        this.unusualBehaviorScore = this.calculateUnusualBehaviourScore();
        this.flowThroughScore = this.calculateFlowThroughScore();
    }

    /***
     * This rule returns the proportion of transactions falling in the interval [0.9, 1[ 
     * of the reporting threshold (ori.aml.reportting-threshold) over the historical 
     * period (ori.aml.historical-length). 
     * @return double between [0,1]
     */
    private double calculateStructuringOverTimeScore() {
        double threshold = ConfigProvider.getConfig().getValue("ori.aml.reporting-threshold", double.class);
        double[] interval = {0.9*threshold, 1.0*threshold};
        int N = in.size() + out.size();
        Long Nin = in.stream()
                    .filter(x -> (x.amount<interval[1]) && (x.amount>=interval[0]))
                    .collect(Collectors.counting());
        Long Nout = in.stream()
                    .filter(x -> (x.amount<interval[1]) && (x.amount>=interval[0]))
                    .collect(Collectors.counting());
        return (Nout + Nin)*1.0/N;
    } 

    /***
     * This rule returns the percentual increase in outgoing transactions over a short
     * window moving average (ori.aml.short-window). It is meant to spot an unusual 
     * increase in outgoing transactions over the moving average window. The score is 
     * set to 0.0 if there was a decrease in outgoing transactions or if the number of 
     * outgoing transactions is low.
     * @return double between [0,1]
     */
    private double calculateUnusualOutgoingVolumeScore() {
        if (out.size()<10) {
            return 0.0;
        }
        int window = ConfigProvider.getConfig().getValue("ori.aml.short-window", int.class);
        LocalDate lastDate = out.get(0).timeStamp
                                    .toInstant()
                                    .atZone(ZoneId.of("GMT"))
                                    .plusDays(1)
                                    .toLocalDate();
        Date[] dates = {java.sql.Date.valueOf(lastDate.minusDays(2L*window)), 
            java.sql.Date.valueOf(lastDate.minusDays(window)), 
            java.sql.Date.valueOf(lastDate)};
        int[] NoutWindow = {0, 0}; 
        for (Transaction transaction: out){
            if ((transaction.timeStamp.after(dates[0])) && (transaction.timeStamp.before(dates[1]))){
                NoutWindow[0] = NoutWindow[0] + 1;
            }
            if ((transaction.timeStamp.after(dates[1])) && (transaction.timeStamp.before(dates[2]))){
                NoutWindow[1] = NoutWindow[1] + 1;
            }
        } 
        return Math.atan(Math.max(NoutWindow[1]*1.0/NoutWindow[0], 0.0))/Math.PI/2.0;
    }     

    /****
     * This rule returns how much does the last moving average transaction deviate from 
     * the average value of transactions.
     * @return double between [0,1]
     */ 
    private double calculateUnusualBehaviourScore() {
        int window = ConfigProvider.getConfig().getValue("ori.aml.short-window", int.class);
        Date fromDate = java.sql.Date.valueOf(out.get(0).timeStamp.toInstant()
                                                .atZone(ZoneId.of("GMT"))
                                                .minusDays(window)
                                                .toLocalDate());
        double windowAvg = out.stream()
                              .filter(x -> x.timeStamp.after(fromDate))
                              .collect(Collectors.summingDouble(x -> x.amount));
        double avg = out.stream()
                            .collect(Collectors.summingDouble(x -> x.amount));
        double std = out.stream()
                        .collect(Collectors.summingDouble(x -> (x.amount-avg)*(x.amount-avg)))
                    /(out.size()-1);
        return Math.atan(Math.max((windowAvg - avg)/std, 0.0))/Math.PI/2.0;
    }     

    /****
     * This rule returns the ratio of total outgoing value and total incoming value over  
     * the window (ori.aml.mid-window). This is supposed to indicate "fake" accounts used 
     * to increase the difficulty of tracing the coin.
     * @return double between [0,1]
     */
    private double calculateFlowThroughScore() {
        int window = ConfigProvider.getConfig().getValue("ori.aml.mid-window", int.class);
        Date lastDate = out.get(0).timeStamp.after(in.get(0).timeStamp) 
                                ? out.get(0).timeStamp 
                                : in.get(0).timeStamp;
        Date fromDate = java.sql.Date.valueOf(lastDate.toInstant()
                                                .atZone(ZoneId.of("GMT"))
                                                .minusDays(window)
                                                .toLocalDate());
        double inValue = in.stream()
                            .filter(x -> x.timeStamp.after(fromDate))
                            .collect(Collectors.summingDouble(x -> x.amount));
        double outValue = out.stream()
                            .filter(x -> x.timeStamp.after(fromDate))
                            .collect(Collectors.summingDouble(x -> x.amount));
        return Math.exp(-Math.pow(outValue/inValue,2)/0.01);
    }     

}