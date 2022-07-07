package com.syntifi.ori.rest;

import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.AccountMapper;
import com.syntifi.ori.model.Account;
import io.quarkus.arc.Unremovable;
import io.quarkus.panache.common.Page;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ori Rest Api for {@link Account} endpoints
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 **/
@Singleton
@Unremovable
@RegisterForReflection
@Tag(name = "Account", description = "Account resources")
public class AccountRestAPI extends AbstractBaseRestApi {

    /**
     * POST method to persist a new account in the DB
     *
     * @param chain
     * @param accountDTO
     * @return Response
     * @throws ORIException
     */
    @POST
    @Transactional
    public Response addAccount(@PathParam("chain") String chain,
                               AccountDTO accountDTO) throws ORIException {

        getChainOr404(chain);

        boolean exists = accountRepository.existsAlready(chain, accountDTO.getHash());
        if (exists) {
            throw new ORIException(accountDTO.getHash() + " exists already", Status.CONFLICT);
        }

        accountDTO.setChainName(chain);
        Account account = AccountMapper.toModel(accountDTO);

        accountRepository.check(account);
        accountRepository.persist(account);

        return Response.ok(new JsonObject()
                        .put("method", "POST")
                        .put("uri", URI.create(String.format("/chain/%s/account/%s", chain, account.getHash()))))
                .build();
    }

    /**
     * GET method to retreive all blocks in the DB. Note that the blocks are
     * sorted in reverse chronological order
     *
     * @param chain
     * @return
     * @throws ORIException
     */
    @GET
    public List<AccountDTO> getAllAccounts(@PathParam("chain") String chain,
                                           @DefaultValue("0") @QueryParam("page") int page,
                                           @DefaultValue("25") @QueryParam("pagesSize") int pageSize) throws ORIException {

        getChainOr404(chain);

        return accountRepository.getAllAccounts(chain)
                .page(Page.of(page, pageSize))
                .list()
                .stream()
                .map(AccountMapper::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * GET method to retrieve a block by it's hash. Hash is given as a hex string.
     *
     * @param chain
     * @param hash
     * @return
     * @throws ORIException
     */
    @GET
    @Path("/{hash}")
    public AccountDTO getAccountByHash(@PathParam("chain") String chain, @PathParam("hash") String hash)
            throws ORIException {

        getChainOr404(chain);

        try {
            Account result = accountRepository.findByChainNameAndHash(chain, hash);
            return AccountMapper.fromModel(result);
        } catch (NoResultException e) {
            throw new ORIException(hash + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE method to remove a specific block given the hash. Hash is given as a
     * hex string
     *
     * @param chain
     * @param hash
     * @return
     * @throws ORIException
     */
    @DELETE
    @Transactional
    @Path("/{hash}")
    public Response deleteAccount(@PathParam("chain") String chain, @PathParam("hash") String hash)
            throws ORIException {

        try {
            Account account = accountRepository.findByChainNameAndHash(chain, hash);
            if (account.getChain().getName().equals(chain)) {
                accountRepository.delete(account);

                return Response.ok(new JsonObject()
                                .put("method", "DELETE")
                                .put("uri", URI.create(String.format("/chain/%s/account/%s", chain, account.getHash()))))
                        .build();
            } else {
                throw new ORIException("Forbidden", 403);
            }
        } catch (NoResultException e) {
            throw new ORIException(hash + " not found", Status.NOT_FOUND);
        } catch (NonUniqueResultException e) {
            throw new ORIException(hash + " not unique", Status.INTERNAL_SERVER_ERROR);
        }
    }
}
