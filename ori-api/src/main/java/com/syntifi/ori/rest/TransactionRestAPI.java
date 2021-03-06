package com.syntifi.ori.rest;

import java.net.URI;
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
import com.syntifi.ori.model.Block;
import com.syntifi.ori.model.Token;
import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.repository.BlockRepository;
import com.syntifi.ori.repository.TransactionRepository;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import io.quarkus.arc.Unremovable;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;

/**
 * Ori Rest Api for {@link Transaction} endpoints
 * TODO: pagination
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@Singleton
@Unremovable
@RegisterForReflection
@Tag(name = "Transaction", description = "Transaction resources")
public class TransactionRestAPI extends AbstractBaseRestApi {

    private static final String NOT_FOUND_TEXT = " not found";

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    BlockRepository blockRepository;

    /**
     * POST method to persist a new transaction in the DB
     * 
     * @param symbol
     * @param transactionDTO
     * @return
     * @throws ORIException
     */
    @POST
    @Transactional
    @Path("/{tokenSymbol}")
    public Response addTransaction(@PathParam("tokenSymbol") String symbol, TransactionDTO transactionDTO)
            throws ORIException {
        boolean exists = transactionRepository.existsAlready(symbol, transactionDTO.getHash());
        if (exists) {
            throw new ORIException(transactionDTO.getHash() + " exists already", Status.CONFLICT);
        }

        Token token = getTokenOr404(symbol);
        transactionDTO.setTokenSymbol(symbol);

        try {
            Block block = blockRepository.findByTokenSymbolAndHash(symbol, transactionDTO.getBlockHash());
            if (!block.getToken().equals(token)) {
                throw new ORIException("Block hash " + transactionDTO.getBlockHash() + " not found for " + symbol,
                        Status.NOT_FOUND);
            }

            Transaction transaction = TransactionMapper.toModel(transactionDTO);

            transactionRepository.check(transaction);
            transactionRepository.persist(transaction);
            return Response
                    .created(URI.create("/transaction/" + symbol + "/hash/" + transaction.getHash()))
                    .build();
        } catch (NoResultException e) {
            throw new ORIException(transactionDTO.getBlockHash() + NOT_FOUND_TEXT, Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(transactionDTO.getBlockHash() + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST method to pesist a list many transactions to the DB at once
     * 
     * @param symbol
     * @param transactionDTOs
     * @return
     * @throws ORIException
     */
    @POST
    @Transactional
    @Path("/{tokenSymbol}/multiple")
    public Response addTransactions(@PathParam("tokenSymbol") String symbol, List<TransactionDTO> transactionDTOs)
            throws ORIException {

        ResponseBuilder response = new ResponseBuilderImpl().status(Status.CREATED);
        for (TransactionDTO transactionDTO : transactionDTOs) {
            boolean exists = transactionRepository.existsAlready(symbol, transactionDTO.getHash());

            if (exists) {
                throw new ORIException(transactionDTO.getHash() + " exists already", Status.CONFLICT);
            }

            var token = getTokenOr404(symbol);
            transactionDTO.setTokenSymbol(symbol);

            var block = blockRepository.findByTokenSymbolAndHash(symbol, transactionDTO.getBlockHash());

            if (!block.getToken().equals(token)) {
                throw new ORIException("Block hash " + transactionDTO.getBlockHash() + " not found for " + symbol,
                        404);
            }

            Transaction transaction = TransactionMapper.toModel(transactionDTO);

            transactionRepository.check(transaction);
            transactionRepository.persist(transaction);
            response.link(URI.create(String.format("/transaction/%s/hash/%s", symbol, transaction.getHash())), "self");
        }

        return response.build();
    }

    /**
     * GET method to retrieve all transactions for a given token symbol from one
     * account to another account
     * 
     * @param symbol
     * @param fromHash
     * @param toHash
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}")
    public List<TransactionDTO> getAllTransactions(@PathParam("tokenSymbol") String symbol,
            @QueryParam("fromAccount") String fromHash, @QueryParam("toAccount") String toHash)
            throws ORIException {
        getTokenOr404(symbol);
        List<Transaction> transactions;
        Account from = fromHash == null ? null : getAccountOr404(symbol, fromHash);
        Account to = toHash == null ? null : getAccountOr404(symbol, toHash);
        if ((from != null) && (to == null)) {
            transactions = transactionRepository.getOutgoingTransactions(symbol, from.getHash());
        } else if ((from == null) && (to != null)) {
            transactions = transactionRepository.getIncomingTransactions(symbol, to.getHash());
        } else if ((from != null) && (to != null)) {
            transactions = transactionRepository.getTransactionsByTokenSymbolAndFromAccountAndToAccount(symbol,
                    from.getHash(),
                    to.getHash());
        } else {
            transactions = transactionRepository.getAllTransactions(symbol);
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
     * @param symbol
     * @param hash
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/{tokenSymbol}/hash/{hash}")
    public TransactionDTO getTransactionByHash(@PathParam("tokenSymbol") String symbol,
            @PathParam("hash") String hash) throws ORIException {
        try {
            Transaction out = transactionRepository.findByTokenSymbolAndHash(symbol, hash);
            return TransactionMapper.fromModel(out);
        } catch (NoResultException nre) {
            throw new ORIException(hash + NOT_FOUND_TEXT, Status.NOT_FOUND);
        } catch (NonUniqueResultException nure) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
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
        List<Transaction> transactions = transactionRepository.getTransactionsByTokenSymbolAndAccount(symbol,
                account.getHash());
        return transactions
                .stream()
                .map(TransactionMapper::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * GET method to retrieve all incoming transactions to a given account
     * 
     * @param symbol
     * @param hash
     * @return
     * @throws ORIException
     */
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

    /**
     * GET method to retrieve all outgoing transactions from a given account
     * 
     * @param symbol
     * @param hash
     * @return
     * @throws ORIException
     */
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
    @Path("/{tokenSymbol}/hash/{hash}")
    public Response delete(@PathParam("tokenSymbol") String symbol,
            @PathParam("hash") String hash) throws ORIException {
        try {
            Transaction transaction = transactionRepository.findByTokenSymbolAndHash(symbol, hash);

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
            throw new ORIException(hash + NOT_FOUND_TEXT, Status.NOT_FOUND);
        } catch (NonUniqueResultException nure) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }
}