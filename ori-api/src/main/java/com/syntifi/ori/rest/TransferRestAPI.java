package com.syntifi.ori.rest;

import com.syntifi.ori.converter.LocalDateTimeFormat;
import com.syntifi.ori.dto.TransferDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.TransferMapper;
import com.syntifi.ori.model.*;
import com.syntifi.ori.repository.BlockRepository;
import com.syntifi.ori.repository.TransferRepository;
import io.quarkus.arc.Unremovable;
import io.quarkus.panache.common.Page;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ori Rest Api for {@link Transfer} endpoints
 * TODO: pagination
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Unremovable
@RegisterForReflection
@Tag(name = "Transfer", description = "Transfers resources")
public class TransferRestAPI extends AbstractBaseRestApi {

    private static final String NOT_FOUND_TEXT = " not found";

    @Inject
    TransferRepository transferRepository;

    @Inject
    BlockRepository blockRepository;

    @Transactional
    private URI _addTransfer(String chain, TransferDTO transferDTO) {

        boolean exists = transferRepository.existsAlready(chain, transferDTO.getHash());
        if (exists) {
            throw new ORIException(transferDTO.getHash() + " exists already", Status.CONFLICT);
        }

        Chain _chain = getChainOr404(chain);
        Token _token = getTokenOr404(chain, transferDTO.getTokenSymbol());
        Account _from = getAccountOr404(chain, transferDTO.getFromHash());
        Account _to = getAccountOr404(chain, transferDTO.getToHash());
        if (!_token.getChain().equals(_chain) || !_from.getChain().equals(_chain) || !_to.getChain().equals(_chain)) {
            throw new ORIException("Token, fromAccount and toAccount must belong to the same chain " + chain,
                    Status.BAD_REQUEST);
        }
        transferDTO.setChainName(chain);

        try {
            Block block = blockRepository.findByChainNameAndHash(chain, transferDTO.getBlockHash());
            if (!block.getChain().equals(_chain)) {
                throw new ORIException("Block hash " + transferDTO.getBlockHash() + " not found for " + chain,
                        Status.NOT_FOUND);
            }

            Token token = tokenRepository.findByChainAndSymbol(chain, transferDTO.getTokenSymbol());
            if (!token.getChain().equals(_chain)) {
                throw new ORIException("Token " + transferDTO.getTokenSymbol() + " not found for " + chain,
                        Status.NOT_FOUND);
            }
            Transfer transfer = TransferMapper.toModel(transferDTO);

            transferRepository.check(transfer);
            transferRepository.persist(transfer);
            return URI.create(String.format("/chain/%s/transfer/%s", chain, transfer.getHash()));
        } catch (NoResultException e) {
            throw new ORIException(transferDTO.getBlockHash() + NOT_FOUND_TEXT, Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(transferDTO.getBlockHash() + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST method to persist a new transfer in the DB
     *
     * @param chain
     * @param transferDTO
     * @return
     * @throws ORIException
     */
    @POST
    @Transactional
    public Response addTransfer(@PathParam("chain") String chain,
                                TransferDTO transferDTO)
            throws ORIException {
        return Response.ok(new JsonObject()
                        .put("method", "POST")
                        .put("uri", _addTransfer(chain, transferDTO)))
                .build();
    }

    /**
     * POST method to pesist a list many transfer to the DB at once
     *
     * @param chain
     * @param transferDTOS
     * @return
     * @throws ORIException
     */
    @POST
    @Transactional
    @Path("/multiple")
    public Response addTransfer(@PathParam("chain") String chain,
                                List<TransferDTO> transferDTOS)
            throws ORIException {

        ResponseBuilder response = new ResponseBuilderImpl().status(Status.CREATED);
        for (TransferDTO transferDTO : transferDTOS) {
            response.link(_addTransfer(chain, transferDTO), "self");
        }
        return response.build();
    }

    /**
     * GET method to retrieve all transfers for a given token symbol from one
     * account to another account
     *
     * @param chain
     * @param accountHash
     * @param coinFlow
     * @param fromDate
     * @param toDate
     * @param page
     * @param pageSize
     * @return
     * @throws ORIException
     */
    @GET
    public List<TransferDTO> getTransfers(@PathParam("chain") String chain,
                                          @QueryParam("account") String accountHash,
                                          @QueryParam("flow") Flow coinFlow,
                                          @QueryParam("fromDate") @LocalDateTimeFormat LocalDateTime fromDate,
                                          @QueryParam("toDate") @LocalDateTimeFormat LocalDateTime toDate,
                                          @DefaultValue("0") @QueryParam("page") int page,
                                          @DefaultValue("25") @QueryParam("pagesSize") int pageSize)
            throws ORIException {
        getChainOr404(chain);
        List<Transfer> transfers;

        Account account = accountHash == null ? null : getAccountOr404(chain, accountHash);
        OffsetDateTime from = fromDate == null ? null : toDate.atOffset(ZoneOffset.of("Z"));
        OffsetDateTime to = toDate == null ? OffsetDateTime.now() : toDate.atOffset(ZoneOffset.of("Z"));
        Flow flow = coinFlow == null ? Flow.BOTH : coinFlow;

        if (account == null) {
            transfers = from == null
                    ? transferRepository.getAllTransfers(chain).page(Page.of(page, pageSize)).list()
                    : transferRepository.getAllTransfers(chain, from, to).page(Page.of(page, pageSize)).list();
        } else {
            if (flow == Flow.OUT) {
                transfers = from == null
                        ? transferRepository.getOutgoingTransfers(chain, accountHash).page(Page.of(page, pageSize)).list()
                        : transferRepository.getOutgoingTransfers(chain, accountHash, from, to).page(Page.of(page, pageSize)).list();
            } else if (flow == Flow.IN) {
                transfers = from == null
                        ? transferRepository.getIncomingTransfers(chain, accountHash).page(Page.of(page, pageSize)).list()
                        : transferRepository.getIncomingTransfers(chain, accountHash, from, to).page(Page.of(page, pageSize)).list();
            } else {
                transfers = from == null
                        ? transferRepository.getAllTransfersForAccount(chain, accountHash).page(Page.of(page, pageSize)).list()
                        : transferRepository.getAllTransfersForAccount(chain, accountHash, from, to).page(Page.of(page, pageSize)).list();
            }
        }
        return transfers
                .stream()
                .map(TransferMapper::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * GET method to retrieve a transfer by it's hash. Hash is given as a hex
     * string.
     *
     * @param chain
     * @param hash
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/{hash}")
    public TransferDTO getTransferByHash(@PathParam("chain") String chain,
                                         @PathParam("hash") String hash) throws ORIException {
        try {
            Transfer out = transferRepository.findByChainNameAndHash(chain, hash);
            return TransferMapper.fromModel(out);
        } catch (NoResultException nre) {
            throw new ORIException(hash + NOT_FOUND_TEXT, Status.NOT_FOUND);
        } catch (NonUniqueResultException nure) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE method to remove a specific transfer for a specifuc token
     * given the hash. Hash is given as a hex string
     *
     * @param chain
     * @param hash
     * @return
     * @throws ORIException
     */
    @DELETE
    @Transactional
    @Path("/{hash}")
    public Response delete(@PathParam("chain") String chain,
                           @PathParam("hash") String hash) throws ORIException {
        try {
            Transfer transfer = transferRepository.findByChainNameAndHash(chain, hash);

            // TODO: Still need this?
            if (transfer.getBlock().getChain().getName().equals(chain)) {
                transferRepository.delete(transfer);
            } else {
                throw new ORIException("Forbidden", 403);
            }
            return Response.ok(new JsonObject()
                            .put("method", "DELETE")
                            .put("uri", URI.create(String.format("/chain/%s/transfer/%s", chain, transfer.getHash()))))
                    .build();
        } catch (NoResultException nre) {
            throw new ORIException(hash + NOT_FOUND_TEXT, Status.NOT_FOUND);
        } catch (NonUniqueResultException nure) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }
}