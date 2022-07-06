package com.syntifi.ori.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.syntifi.ori.model.Transfer;
import com.syntifi.ori.repository.TransferRepository;

import io.quarkus.panache.common.Page;
import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

/**
 * Service for Transfer business logic
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Singleton
public class TransferService {

    private int maxGraphLength = ConfigProvider.getConfig().getValue("ori.aml.max-trace-coin-length", int.class);
    private static final String TIMESTAMP_FIELD = "timeStamp";

    @Inject
    TransferRepository transferRepository;

    /**
     * Method to retrieve the chain of transfers that end up in the given account
     * during the period fromDate to toDate
     * 
     * @param chainName
     * @param account
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<Transfer> reverseGraphWalk(String chainName, String account, OffsetDateTime fromDate,
                                           OffsetDateTime toDate) {
        int size = 100;
        PanacheQuery<Transfer> allTransfers = transferRepository.getAllTransfers(
                chainName, fromDate, toDate,
                Sort.descending(TIMESTAMP_FIELD));
        Set<String> nodes = new HashSet<>();
        nodes.add(account);
        List<Transfer> graph = new ArrayList<>();
        List<Transfer> transfers = allTransfers.page(Page.ofSize(100)).list();
        while (graph.size() <= maxGraphLength) {
            for (Transfer transfer: transfers) {
                if (transfer.getToAccount() != null && nodes.contains(transfer.getToAccount().getHash())) {
                    nodes.add(transfer.getFromAccount().getHash());
                    graph.add(transfer);
                }
            }
            if (allTransfers.hasNextPage()) {
                transfers = allTransfers.nextPage().list();
            } else {
                break;
            }
        }
        return graph;
    }

    /**
     * Method to retrieve the chain of transfers that track the destination of
     * the funds originating from the given account during the period fromDate to
     * toDate
     * 
     * @param chainName
     * @param account
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<Transfer> forwardGraphWalk(String chainName, String account, OffsetDateTime fromDate,
                                           OffsetDateTime toDate) {
        int size = 100;
        PanacheQuery<Transfer> allTransfers = transferRepository.getAllTransfers(
                chainName, fromDate, toDate,
                Sort.ascending(TIMESTAMP_FIELD));
        Set<String> nodes = new HashSet<>();
        nodes.add(account);
        List<Transfer> graph = new ArrayList<>();
        List<Transfer> transfers = allTransfers.page(Page.ofSize(100)).list();
        while (graph.size() <= maxGraphLength) {
            for (Transfer transfer : transfers) {
                if (transfer.getToAccount() != null && nodes.contains(transfer.getFromAccount().getHash())) {
                    nodes.add(transfer.getToAccount().getHash());
                    graph.add(transfer);
                }
            }
            if (allTransfers.hasNextPage()) {
                transfers = allTransfers.nextPage().list();
            } else {
                break;
            }
        }
        return graph;
    }
}
