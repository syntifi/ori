
package com.syntifi.ori.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.syntifi.ori.dto.TransactionDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.AccountRepository;
import com.syntifi.ori.repository.BlockRepository;
import com.syntifi.ori.repository.TokenRepository;
import com.syntifi.ori.repository.TransactionRepository;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.vertx.core.json.JsonObject;

/**
 * REST API transaction endpoints
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Transaction", description = "Transaction resources")
public class TransactionRestAPI {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    BlockRepository blockRepository;

    @Inject
    TokenRepository tokenRepository;

    @Inject
    AccountRepository accountRepository;

    private Token getToken(String symbol) {
        Token token = tokenRepository.findBySymbol(symbol);
        if (token == null) {
            throw new ORIException("Token not found", 404);
        }
        return token;
    }

    private Account getAccount(String hash) {
        Account account = accountRepository.findByHash(hash);
        if (account == null) {
            throw new ORIException("Account not found", 404);
        }
        return account;
    }

    /**
     * POST method to add and index a new transactions in ES
     * 
     * @param transaction
     * @return Response
     * @throws ORIException
     */
    @POST
    @Transactional
    @Path("/{tokenSymbol}/block/{blockHash}/from/{fromAccount}/to/{toAccount}")
    public Response addTransaction(Transaction transaction, @PathParam("tokenSymbol") String symbol,
            @PathParam("blockHash") String blockHash, @PathParam("fromAccount") String fromHash,
            @PathParam("toAccount") String toHash)
            throws ORIException {
        boolean exists = transactionRepository.existsAlready(transaction);
        if (exists) {
            throw new ORIException(transaction.getHash() + " exists already", 400);
        }
        var token = getToken(symbol);
        var block = blockRepository.findByHash(blockHash);
        if (block == null) {
            throw new ORIException("Block not found", 404);
        }
        if (!block.getToken().equals(token)) {
            throw new ORIException("Block hash " + blockHash + " not found for " + symbol, 404);
        }
        Account fromAccount = getAccount(fromHash);
        Account toAccount = getAccount(toHash);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setBlock(block);
        transactionRepository.check(transaction);
        transactionRepository.persist(transaction);
        return Response
                .ok(new JsonObject().put("created", URI.create("/transaction/" + symbol + "/hash/" + transaction.hash)))
                .build();
    }

    /**
     * GET method to retreive all transactions indexed in ES originating from one
     * account
     * to another account. Note that the transactions are sorted in reverse
     * chronological order
     * 
     * @param from
     * @param to
     * @return List<Transaction>
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}")
    public List<TransactionDTO> getAllTransactions(@PathParam("tokenSymbol") String symbol,
            @QueryParam("fromAccount") String fromHash, @QueryParam("toAccount") String toHash,
            @QueryParam("blockHash") String block) throws ORIException {
        List<Transaction> transactions = new ArrayList<>();
        Account from = fromHash == null ? null : getAccount(fromHash);
        Account to = toHash == null ? null : getAccount(toHash);
        if ((from != null) && (to == null)) {
            transactions = transactionRepository.getOutgoingTransactions(from);
        } else if ((from == null) && (to != null)) {
            transactions = transactionRepository.getIncomingTransactions(to);
        } else if ((from != null) && (to != null)) {
            transactions = transactionRepository.getTransactionsFromAccountToAccount(from, to);
        } else {
            transactions = transactionRepository.getAllTransactions();
        }
        // TODO: PAGINATION HERE
        return transactions
                .stream()
                .filter(t -> t.getBlock().getToken().getSymbol().equals(symbol))
                .map(TransactionDTO::fromModel)
                .collect(Collectors.toList())
                .subList(0, Math.min(100, transactions.size()));
    }

    /**
     * GET method to retrieve a transaction by it's hash. Hash is given as a hex
     * string.
     * 
     * @param hash
     * @return Transaction
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}/hash/{transactionHash}")
    public TransactionDTO getTransactionByHash(@PathParam("tokenSymbol") String symbol,
            @PathParam("transactionHash") String hash) throws ORIException {
        Transaction out = transactionRepository.findByHash(hash);
        if (out == null || !out.getBlock().getToken().getSymbol().equals(symbol)) {
            throw new ORIException(hash + " not found", 404);
        }
        return TransactionDTO.fromModel(out);
    }

    /**
     * GET method to retrieve all incoming and outgoing transactions in a given
     * account
     * 
     * @param symbol
     * @param account
     * @return
     * @throws ORIException
     */
    @GET
    @Path("{tokenSymbol}/account/{account}")
    public List<TransactionDTO> getTransactionsByAccount(@PathParam("tokenSymbol") String symbol,
            @PathParam("account") String hash) throws ORIException {
        Account account = getAccount(hash);
        List<Transaction> transactions = transactionRepository.getTransactionsByAccount(account);
        return transactions
                .stream()
                .filter(t -> t.getBlock().getToken().getSymbol().equals(symbol))
                .map(TransactionDTO::fromModel)
                .collect(Collectors.toList())
                .subList(0, Math.min(100, transactions.size()));
    }

    @GET
    @Path("{tokenSymbol}/incoming/account/{account}")
    public List<TransactionDTO> getIncomingTransactionsToAccount(@PathParam("tokenSymbol") String symbol,
            @PathParam("account") String hash) throws ORIException {
        Account account = getAccount(hash);
        List<Transaction> transactions = transactionRepository.getIncomingTransactions(account);
        return transactions
                .stream()
                .filter(t -> t.getBlock().getToken().getSymbol().equals(symbol))
                .map(TransactionDTO::fromModel)
                .collect(Collectors.toList())
                .subList(0, Math.min(100, transactions.size()));
    }

    @GET
    @Path("{tokenSymbol}/outgoing/account/{account}")
    public List<TransactionDTO> getOutgoingTransactionsFromAccount(@PathParam("tokenSymbol") String symbol,
            @PathParam("account") String hash) throws ORIException {
        Account account = getAccount(hash);
        var transactions = transactionRepository.getOutgoingTransactions(account);
        return transactions
                .stream()
                .filter(t -> t.getBlock().getToken().getSymbol().equals(symbol))
                .map(TransactionDTO::fromModel)
                .collect(Collectors.toList())
                .subList(0, Math.min(100, transactions.size()));
    }

    /**
     * DELETE method to remove a specific transaction for a specifuc token
     * given the hash. Hash is given as a hex string
     * 
     * @param symbol
     * @param hash
     * @return
     * @throws ORIException
     */
    @DELETE
    @Transactional
    @Path("/{tokenSymbol}/hash/{transactionHash}")
    public Response delete(@PathParam("tokenSymbol") String symbol,
            @PathParam("transactionHash") String hash) throws ORIException {
        Transaction transaction = transactionRepository.findByHash(hash);
        if (transaction == null) {
            throw new ORIException(symbol + " not found", 404);
        }
        if (transaction.getBlock().getToken().getSymbol().equals(symbol)) {
            transactionRepository.delete(transaction);
        } else {
            throw new ORIException("Forbidden", 403);
        }
        return Response.ok(new JsonObject()
                .put("method", "DELETE")
                .put("uri", "/transaction/" + symbol + "/hash/" + hash))
                .build();
    }
}