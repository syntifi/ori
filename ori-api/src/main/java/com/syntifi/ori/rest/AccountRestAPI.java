package com.syntifi.ori.rest;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.syntifi.ori.dto.AccountDTO;
import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.mapper.AccountMapper;
import com.syntifi.ori.model.Account;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.panache.common.Sort;
import io.vertx.core.json.JsonObject;

/**
 * REST API account endpoints
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@Singleton
@Tag(name = "Account", description = "Account resources")
public class AccountRestAPI extends AbstractBaseRestApi {

   /**
    * POST method to add a new account and index in ES
    * 
    * @param symbol
    * @param accountDTO
    * @return Response
    * @throws ORIException
    */
   @POST
   @Transactional
   @Path("/{tokenSymbol}")
   public Response addAccount(@PathParam("tokenSymbol") String symbol, AccountDTO accountDTO) throws ORIException {

      getTokenOr404(symbol);

      accountDTO.setTokenSymbol(symbol);
      Account account = AccountMapper.toModel(accountDTO, tokenRepository);

      boolean exists = accountRepository.existsAlready(symbol, account);
      if (exists) {
         throw new ORIException(account.getHash() + " exists already", 400);
      }

      accountRepository.check(account);
      accountRepository.persist(account);

      return Response.ok(new JsonObject().put("created",
            URI.create("/account/" + symbol + "/" + account.getHash())))
            .build();
   }

   /**
    * GET method to retreive all blocks indexed in ES. Note that the blocks are
    * sorted in reverse chronological order
    * 
    * @return a list of {@link AccountDTO}
    * @throws ORIException
    */
   @GET
   @Path("/{tokenSymbol}")
   public List<AccountDTO> getAllAccounts(@PathParam("tokenSymbol") String symbol) throws ORIException {

      getTokenOr404(symbol);

      return accountRepository.listAll(Sort.descending("hash")).stream().map(AccountMapper::fromModel)
            .collect(Collectors.toList());
   }

   /**
    * GET method to retrieve a block by it's hash. Hash is given as a hex string.
    * 
    * @param hash
    * @return Block
    * @throws ORIException
    */
   @GET
   @Path("/{tokenSymbol}/hash/{hash}")
   public AccountDTO getAccountByHash(@PathParam("tokenSymbol") String symbol, @PathParam("hash") String hash)
         throws ORIException {

      getTokenOr404(symbol);

      try {
         Account result = accountRepository.findByHashAndTokenSymbol(symbol, hash);
         return AccountMapper.fromModel(result);
      } catch (NoResultException e) {
         throw new ORIException(hash + " not found", 404);
      } catch (NonUniqueResultException e) {
         throw new ORIException(hash + " not unique", 500);
      }
   }

   /**
    * DELETE method to remove a specific block given the hash. Hash is given as a
    * hex string
    * 
    * @param hash
    * @return
    * @throws ORIException
    */
   @DELETE
   @Transactional
   @Path("/{tokenSymbol}/hash/{hash}")
   public Response deleteAccount(@PathParam("tokenSymbol") String symbol, @PathParam("hash") String hash)
         throws ORIException {

      try {
         Account account = accountRepository.findByHashAndTokenSymbol(symbol, hash);
         if (account.getToken().getSymbol().equals(symbol)) {
            accountRepository.delete(account);

            return Response.ok(new JsonObject()
                  .put("method", "DELETE")
                  .put("uri", "/account/" + symbol + "/hash/" + hash))
                  .build();
         } else {
            throw new ORIException("Forbidden", 403);
         }
      } catch (NoResultException e) {
         throw new ORIException(hash + " not found", 404);
      } catch (NonUniqueResultException e) {
         throw new ORIException(hash + " not unique", 500);
      }
   }
}
