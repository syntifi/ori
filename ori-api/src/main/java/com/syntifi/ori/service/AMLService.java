package com.syntifi.ori.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import com.syntifi.ori.RiskMetrics;
import com.syntifi.ori.dto.AMLRulesDTO;
import com.syntifi.ori.dto.TransactionDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TransactionMapper;
import com.syntifi.ori.model.Transaction;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 * AML rules calculator
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
public class AMLService {

    private static int minNumberOfTransactions = ConfigProvider.getConfig()
            .getValue("ori.aml.min-number-transactions", int.class);

    /**
     * Method to calculate the risk metrics scores and return a AMLRulesDTO object
     *  
     * @param in
     * @param out
     * @param thresh
     * @param sWindow
     * @param lWindow
     * @return
     * @throws ORIException
     */
    public AMLRulesDTO calculateScores(List<Transaction> in, List<Transaction> out, Double thresh,
            Integer sWindow, Integer lWindow)
            throws ORIException {
        sanityCheck(in, out);
        double threshold = thresh == null
                ? ConfigProvider.getConfig().getValue("ori.aml.reporting-threshold", double.class)
                : thresh;
        int shortWindow = sWindow == null
                ? ConfigProvider.getConfig().getValue("ori.aml.short-window", int.class)
                : sWindow;
        int longWindow = lWindow == null
                ? ConfigProvider.getConfig().getValue("ori.aml.long-window", int.class)
                : lWindow;

        AMLRulesDTO scores = new AMLRulesDTO();
        List<TransactionDTO> inDTO = in.stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());
        List<TransactionDTO> outDTO = out.stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());

        scores.setFlowThroughScore(
                RiskMetrics.calculateFlowThroughScore(inDTO, outDTO, longWindow,
                        minNumberOfTransactions));
        scores.setStructuringOverTimeScore(
                RiskMetrics.calculateStructuringOverTimeScore(inDTO, outDTO, threshold,
                        minNumberOfTransactions));
        scores.setUnusualBehaviorScore(
                RiskMetrics.calculateUnusualBehaviourScore(outDTO, shortWindow,
                        minNumberOfTransactions));
        scores.setUnusualOutgoingVolumeScore(
                RiskMetrics.calculateUnusualOutgoingVolumeScore(outDTO, shortWindow, longWindow,
                        minNumberOfTransactions));
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
}
