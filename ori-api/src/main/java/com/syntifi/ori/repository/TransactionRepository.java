package com.syntifi.ori.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Transaction;

@ApplicationScoped
public class TransactionRepository implements Repository<Transaction> {

    public Transaction findByHash(String hash) {
        List<Transaction> result = list("hash", hash);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean existsAlready(Transaction transaction) {
        return findByHash(transaction.getHash()) != null;
    }

    public List<Transaction> getOutgoingTransactions(Account account) {
        return list("fromAccount", account); 
    }

    public List<Transaction> getIncomingTransactions(Account account) {
        return list("toAccount", account); 
    }

    public List<Transaction> getTransactionsFromAccountToAccount(Account fromAccount, Account toAccount) {
        return list("fromAccount = ?1 and toAccount = ?2", fromAccount, toAccount); 
    }

    public List<Transaction> getTransactionsByAccount(Account account) {
        return list("fromAccount = ?1 or toAccount = ?1", account); 
    }

    public List<Transaction> getAllTransactions() {
        return listAll();
    }

}
