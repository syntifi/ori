package com.syntifi.ori.service;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.syntifi.ori.model.Transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AMLServiceTest {

    @Inject
    AMLService amlService;

    private static List<Transaction> in;
    private static List<Transaction> out;

    @BeforeAll
    public static void createTransactions() {
        in = new LinkedList<>();
        out= new LinkedList<>();
    }

    @Test
    public void testcalculateScores() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testCalculateStructuringOverTimeScore() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testCalculateUnusualOutgoingVolumeScore() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testCalculateUnusualBehaviourScore() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testCalculateFlowThroughScore() {
        Assertions.assertTrue(true);
    }

}
