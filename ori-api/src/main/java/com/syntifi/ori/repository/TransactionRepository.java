package com.syntifi.ori.repository;

import javax.enterprise.context.ApplicationScoped;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction>, Repository<Transaction> {

    public Transaction findByHash(String hash) throws ORIException {
        Transaction result = find("hash", hash).firstResult();
        if (result == null) {
            throw new ORIException("Transaction hash " + hash + " not found!", 404);
        }
        return result;
    }
}
