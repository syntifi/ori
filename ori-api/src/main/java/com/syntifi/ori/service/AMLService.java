package com.syntifi.ori.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import com.syntifi.ori.RiskMetrics;
import com.syntifi.ori.dto.AMLRulesDTO;
import com.syntifi.ori.dto.TransferDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TransferMapper;
import com.syntifi.ori.model.Transfer;

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

    private static int minNumberOfTransfers = ConfigProvider.getConfig()
            .getValue("ori.aml.min-number-transfers", int.class);

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
    public AMLRulesDTO calculateScores(List<Transfer> in, List<Transfer> out, Double thresh,
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
        List<TransferDTO> inDTO = in.stream()
                .map(TransferMapper::fromModel)
                .collect(Collectors.toList());
        List<TransferDTO> outDTO = out.stream()
                .map(TransferMapper::fromModel)
                .collect(Collectors.toList());

        scores.setFlowThroughScore(
                RiskMetrics.calculateFlowThroughScore(inDTO, outDTO, longWindow,
                        minNumberOfTransfers));
        scores.setStructuringOverTimeScore(
                RiskMetrics.calculateStructuringOverTimeScore(inDTO, outDTO, threshold,
                        minNumberOfTransfers));
        scores.setUnusualBehaviorScore(
                RiskMetrics.calculateUnusualBehaviourScore(outDTO, shortWindow,
                        minNumberOfTransfers));
        scores.setUnusualOutgoingVolumeScore(
                RiskMetrics.calculateUnusualOutgoingVolumeScore(outDTO, shortWindow, longWindow,
                        minNumberOfTransfers));
        return scores;
    }

    // TODO: Throw an ORIException in a service? Does it make sense?
    private void sanityCheck(List<Transfer> in, List<Transfer> out) throws ORIException {
        int n = (in == null ? 0 : in.size()) + (out == null ? 0 : out.size());
        if (n == 0) {
            throw new ORIException(
                    "Unable to calculate scores because the given account does not contain any transfer",
                    Status.NOT_FOUND.getStatusCode());
        }
    }
}
