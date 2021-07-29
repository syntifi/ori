package com.syntifi.ori.service;

import java.util.List;

import com.syntifi.ori.model.Transaction;

public class AMLRules {

    private List<Transaction> transactions;

    public AMLRules(){}

    public AMLRules(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Double structuringOverTimeScore() {
        return 0.0;
    } 

    public Double highVolumeScore() {
        return 0.0;
    }     

    public Double unusualVolumeScore() {
        return 0.0;
    }     
    
    public Double unusualBehaviourScore() {
        return 0.0;
    }     

    public Double flowThroughScore() {
        return 0.0;
    }     

}
