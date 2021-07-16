package com.syntifi.ori.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.syntifi.ori.model.Transaction;

import org.jboss.logging.Logger;

@ApplicationScoped
public class TransactionService {
    private static final Logger LOG = Logger.getLogger(TransactionService.class);

    @Inject
    RestClient restClient;

    public void index(Transaction transaction) throws IOException {
        Request request = new Request("POST", "/transaction/_doc/" + transaction.hash);
        request.setJsonEntity(JsonObject.mapFrom(transaction).toString());
        restClient.performRequest(request);
    }

    public Response clear() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        Request request = new Request("POST", "/transaction/_delete_by_query?conflicts=proceed");
        request.setJsonEntity(queryJson.encode());
        return restClient.performRequest(request);
    }

    public Response delete(String hash) throws IOException {
        JsonObject matchJson = new JsonObject().put("match", new JsonObject().put("hash", hash));
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        Request request = new Request("POST", "/transaction/_delete_by_query?conflicts=proceed");
        request.setJsonEntity(queryJson.encode());
        return restClient.performRequest(request);
        // Request request = new Request("DELETE", "/transaction/" + hash);
        // return restClient.performRequest(request);
    }

    public Transaction getTransactionByHash(String hash) throws IOException {
        Request request = new Request("GET", "/transaction/_doc/" + hash);
        Response response = restClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject json = new JsonObject(responseBody);
        return json.getJsonObject("_source").mapTo(Transaction.class);
    }

    // public List<Transaction> getAllTransactions(int size) throws IOException {
    public List<Transaction> getAllTransactions() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        // queryJson.put("size", size);
        return queryTransaction(queryJson);
    }

    public List<Transaction> getOutgoingTransactions(String from) throws IOException {
        return search("from", from);
    }

    public List<Transaction> getIncomingTransactions(String to) throws IOException {
        return search("to", to);
    }

    public List<Transaction> getAllTransactionsByAccount(String account) throws IOException {
        return getFromToTransactions(account, account, "should");
    }

    public List<Transaction> getTransactionsFromAccountToAccount(String from, String to) throws IOException {
        return getFromToTransactions(from, to, "must");
    }

    public List<Transaction> getFromToTransactions(String from, String to, String term) throws IOException {
        // construct a JSON query like {"query": {"bool": {"should": [{"term":
        // {"<term1>": "<match>"}}, ..]}}}
        JsonObject fromJson = new JsonObject().put("from", from);
        JsonObject toJson = new JsonObject().put("to", to);
        JsonArray shouldTerm = new JsonArray();
        shouldTerm.add(new JsonObject().put("match_phrase", fromJson));
        shouldTerm.add(new JsonObject().put("match_phrase", toJson));
        JsonObject termJson = new JsonObject().put(term, shouldTerm);
        if (term.equals("should"))
            termJson.put("minimum_should_match", 1);
        JsonObject matchJson = new JsonObject().put("bool", termJson);
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        LOG.info("===============");
        LOG.info(queryJson.toString());
        return queryTransaction(queryJson);
    }

    private List<Transaction> search(String term, String match) throws IOException {
        // construct a JSON query like {"query": {"match": {"<term>": "<match>"}}}
        JsonObject termJson = new JsonObject().put(term, match);
        JsonObject matchJson = new JsonObject().put("match_phrase", termJson);
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        return queryTransaction(queryJson);
    }

    private List<Transaction> queryTransaction(JsonObject queryJson) throws IOException {
        JsonArray sortTerm = new JsonArray();
        sortTerm.add(new JsonObject().put("timeStamp", "desc"));
        queryJson.put("sort", sortTerm);
        Request request = new Request("GET", "/transaction/_search?scroll=1m");
        request.setJsonEntity(queryJson.encode());
        Response response = restClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject json = new JsonObject(responseBody);
        long total = json.getJsonObject("hits").getJsonObject("total").getLong("value");
        List<Transaction> results = new ArrayList<>();
        JsonArray hits = json.getJsonObject("hits").getJsonArray("hits");
        for (int i = 0; i < hits.size(); i++) {
            JsonObject hit = hits.getJsonObject(i);
            Transaction transaction = hit.getJsonObject("_source").mapTo(Transaction.class);
            results.add(transaction);
        }
        return results;
    }
}
