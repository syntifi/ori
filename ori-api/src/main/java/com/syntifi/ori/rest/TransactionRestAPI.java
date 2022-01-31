
package com.syntifi.ori.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.syntifi.ori.dto.TransactionDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TransactionMapper;
import com.syntifi.ori.model.Account;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.AccountRepository;
import com.syntifi.ori.repository.BlockRepository;
import com.syntifi.ori.repository.TokenRepository;
import com.syntifi.ori.repository.TransactionRepository;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import io.vertx.core.json.JsonObject;

//TODO: pagination
/**
 * REST API transaction endpoints
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Transaction", description = "Transaction resources")
public class TransactionRestAPI extends AbstractBaseRestApi {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    BlockRepository blockRepository;

    @Inject
    TokenRepository tokenRepository;

    @Inject
    AccountRepository accountRepository;

    /**
     * POST method to add and index a new transactions in ES
     * 
     * @param symbol
     * @param transactionDTO
     * @return Response
     * @throws ORIException
     */
    @POST
    @Transactional
    @Path("/{tokenSymbol}")
    public Response addTransaction(@PathParam("tokenSymbol") String symbol, TransactionDTO transactionDTO)
            throws ORIException {
        boolean exists = transactionRepository.existsAlready(symbol, transactionDTO.getHash());
        if (exists) {
            throw new ORIException(transactionDTO.getHash() + " exists already", 400);
        }
        var token = getTokenOr404(symbol);
        transactionDTO.setTokenSymbol(symbol);
        var block = blockRepository.findByHash(symbol, transactionDTO.getBlockHash());
        if (block == null) {
            throw new ORIException("Block not found", 404);
        }
        if (!block.getToken().equals(token)) {
            throw new ORIException("Block hash " + transactionDTO.getBlockHash() + " not found for " + symbol, 404);
        }

        Transaction transaction = TransactionMapper.toModel(transactionDTO, accountRepository, blockRepository);

        transactionRepository.check(transaction);
        transactionRepository.persist(transaction);
        return Response
                .created(URI.create("/transaction/" + symbol + "/hash/" + transaction.getHash()))
                .build();
    }

    @POST
    @Transactional
    @Path("/{tokenSymbol}/multiple")
    public Response addTransactions(@PathParam("tokenSymbol") String symbol, List<TransactionDTO> transactionDTOs)
            throws ORIException {
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionDTO transactionDTO : transactionDTOs) {
            boolean exists = transactionRepository.existsAlready(symbol, transactionDTO.getHash());
            if (exists) {
                throw new ORIException(transactionDTO.getHash() + " exists already", 400);
            }
            var token = getTokenOr404(symbol);
            var block = blockRepository.findByHash(symbol, transactionDTO.getBlockHash());
            if (block == null) {
                throw new ORIException("Block not found", 404);
            }
            if (!block.getToken().equals(token)) {
                throw new ORIException("Block hash " + transactionDTO.getBlockHash() + " not found for " + symbol, 404);
            }

            Transaction transaction = TransactionMapper.toModel(transactionDTO, accountRepository, blockRepository);

            transactionRepository.check(transaction);

            transactions.add(transaction);
        }

        transactionRepository.persist(transactions);

        ResponseBuilder response = new ResponseBuilderImpl().status(Status.CREATED);
        for (Transaction transaction : transactions) {
            response.link(URI.create(String.format("/transaction/%s/hash/%s", symbol, transaction.getHash())), "self");
        }

        return response.build();
    }

    /**
     * GET method to retreive all transactions indexed in ES originating from one
     * account
     * to another account. Note that the transactions are sorted in reverse
     * chronological order
     * 
     * @param symbol
     * @param fromHash
     * @param toHash
     * @param block
     * @return a list of {@link Transaction}
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}")
    public List<TransactionDTO> getAllTransactions(@PathParam("tokenSymbol") String symbol,
            @QueryParam("fromAccount") String fromHash, @QueryParam("toAccount") String toHash,
            @QueryParam("blockHash") String block) throws ORIException {
        List<Transaction> transactions = new ArrayList<>();
        Account from = fromHash == null ? null : getAccountOr404(symbol, fromHash);
        Account to = toHash == null ? null : getAccountOr404(symbol, toHash);
        if ((from != null) && (to == null)) {
            transactions = transactionRepository.getOutgoingTransactions(symbol, from.getHash());
        } else if ((from == null) && (to != null)) {
            transactions = transactionRepository.getIncomingTransactions(symbol, to.getHash());
        } else if ((from != null) && (to != null)) {
            transactions = transactionRepository.getTransactionsFromAccountToAccount(symbol, from.getHash(),
                    to.getHash());
        } else {
            transactions = transactionRepository.getAllTransactions();
        }
        // TODO: PAGINATION HERE
        return transactions
                .stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());
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
        try {
            Transaction out = transactionRepository.findByHash(symbol, hash);
            return TransactionMapper.fromModel(out);
        } catch (NoResultException nre) {
            throw new ORIException(hash + " not found", 404);
        } catch (NonUniqueResultException nure) {
            throw new ORIException(hash + " found more than once", 500);
        }
    }

    /**
     * GET method to retrieve all incoming and outgoing transactions in a given
     * account
     * 
     * @param symbol
     * @param hash
     * @return
     * @throws ORIException
     */
    @GET
    @Path("{tokenSymbol}/account/{account}")
    public List<TransactionDTO> getTransactionsByAccount(@PathParam("tokenSymbol") String symbol,
            @PathParam("account") String hash) throws ORIException {
        Account account = getAccountOr404(symbol, hash);
        List<Transaction> transactions = transactionRepository.getTransactionsByAccount(symbol, account.getHash());
        return transactions
                .stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());
    }

    @GET
    @Path("{tokenSymbol}/incoming/account/{account}")
    public List<TransactionDTO> getIncomingTransactionsToAccount(@PathParam("tokenSymbol") String symbol,
            @PathParam("account") String hash) throws ORIException {
        Account account = getAccountOr404(symbol, hash);
        List<Transaction> transactions = transactionRepository.getIncomingTransactions(symbol, account.getHash());
        return transactions
                .stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());
    }

    @GET
    @Path("{tokenSymbol}/outgoing/account/{account}")
    public List<TransactionDTO> getOutgoingTransactionsFromAccount(@PathParam("tokenSymbol") String symbol,
            @PathParam("account") String hash) throws ORIException {
        Account account = getAccountOr404(symbol, hash);
        var transactions = transactionRepository.getOutgoingTransactions(symbol, account.getHash());
        return transactions
                .stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());
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
        try {
            Transaction transaction = transactionRepository.findByHash(symbol, hash);

            // TODO: Still need this?
            if (transaction.getBlock().getToken().getSymbol().equals(symbol)) {
                transactionRepository.delete(transaction);
            } else {
                throw new ORIException("Forbidden", 403);
            }
            return Response.ok(new JsonObject()
                    .put("method", "DELETE")
                    .put("uri", "/transaction/" + symbol + "/hash/" + hash))
                    .build();
        } catch (NoResultException nre) {
            throw new ORIException(hash + " not found", 404);
        } catch (NonUniqueResultException nure) {
            throw new ORIException(hash + " found more than once", 500);
        }
    }
}