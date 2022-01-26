package com.syntifi.ori.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Transaction;

import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * All transaction related services for querying ES using the low level API
 * 
 * @author Andre Bertolace
 * @since 0.1.0
 */
@ApplicationScoped
public class TransactionService {
    private int maxGraphLength = ConfigProvider.getConfig().getValue("ori.aml.max-trace-coin-length", int.class);

    @Inject
    RestClient restClient;

    /**
     * Indexes a new transaction document on Elastic Search
     * 
     * @param transaction
     * @throws IOException
     */
    public void index(Transaction transaction) throws IOException {
        Request request = new Request("POST", "/transaction/_doc/" + transaction.getHash());
        request.setJsonEntity(JsonObject.mapFrom(transaction).toString());
        restClient.performRequest(request);
    }

    /**
     * Removes all transation documents indexed in ES
     * 
     * @return ES Response
     * @throws IOException
     */
    public Response clear() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        Request request = new Request("POST", "/transaction/_delete_by_query?conflicts=proceed");
        request.setJsonEntity(queryJson.encode());
        return restClient.performRequest(request);
    }

    /**
     * Deletes a transation given its hash
     * 
     * @param hash
     * @return ES Response
     * @throws IOException
     */
    public Response delete(String hash) throws IOException {
        Request request = new Request("DELETE", "/transaction/_doc/" + hash);
        return restClient.performRequest(request);
    }

    /**
     * Retrieves a transaction given its hash
     * 
     * @param hash
     * @return Transaction
     * @throws IOException
     */
    public Transaction getTransactionByHash(String hash) throws IOException {
        Request request = new Request("GET", "/transaction/_doc/" + hash);
        Response response = restClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject json = new JsonObject(responseBody);
        return json.getJsonObject("_source").mapTo(Transaction.class);
    }

    /**
     * Retrieves all transactions indexed in the database sorted by date in
     * descending order
     * 
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> getAllTransactions() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        // queryJson.put("size", size);
        return queryTransaction(queryJson, "desc");
    }

    /**
     * Retrieves all ongoing transactions from a given account
     * 
     * @param from
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> getOutgoingTransactions(String from) throws IOException {
        return search("from", from);
    }

    /**
     * Retrieves all ongoing transactions from a given account in a given period
     * [fromDate, toDate]
     * 
     * @param from
     * @param fromDate
     * @param toDate
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> getOutgoingTransactions(String from, LocalDateTime fromDate, LocalDateTime toDate)
            throws IOException {
        return search("from", from, fromDate, toDate);
    }

    /**
     * Retrieves all incoming transactions to a given account
     * 
     * @param to
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> getIncomingTransactions(String to) throws IOException {
        return search("to", to);
    }

    /**
     * Retrieves all incoming transactions to a given account in a given period
     * [fromDate, toDate]
     * 
     * @param to
     * @param fromDate
     * @param toDate
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> getIncomingTransactions(String to, LocalDateTime fromDate, LocalDateTime toDate)
            throws IOException {
        return search("to", to, fromDate, toDate);
    }

    /**
     * Retrieves all incoming and outgoing transactions in a given account
     * 
     * @param account
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> getAllTransactionsByAccount(String account) throws IOException {
        return getFromToTransactions(account, account, "should");
    }

    /**
     * Retrieves all transactions from one account to annother account
     * 
     * @param from
     * @param to
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> getTransactionsFromAccountToAccount(String from, String to) throws IOException {
        return getFromToTransactions(from, to, "must");
    }

    /**
     * Feature to walk backwards in the graph an trace the origin of the coin
     * landing in
     * the given account in a given period [fromDate, toDate]
     * 
     * @param account
     * @param fromDate
     * @param toDate
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> reverseGraphWalk(String account, LocalDateTime fromDate, LocalDateTime toDate)
            throws IOException {
        return graphWalk(account, fromDate, toDate, "desc");
    }

    /**
     * Feature to walk forward in the graph an trace the destination of the coin
     * leaving the
     * given account in a given period [fromDate, toDate]
     * 
     * @param account
     * @param fromDate
     * @param toDate
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> forwardGraphWalk(String account, LocalDateTime fromDate, LocalDateTime toDate)
            throws IOException {
        return graphWalk(account, fromDate, toDate, "asc");
    }

    /**
     * Base feature to walk back/forward the graph. Note that it uses the fact that
     * the transactions
     * are sorted in reverse chronological order.
     * 
     * @param account
     * @param fromDate
     * @param toDate
     * @param direction
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> graphWalk(String account, LocalDateTime fromDate, LocalDateTime toDate, String direction)
            throws IOException {
        // construct a Json query like "query": { "range": { "timestamp": {
        // "gte": <fromDate>, "lte": <toDate> } } }
        JsonObject dateRangeJson = new JsonObject().put("gte", fromDate);
        dateRangeJson.put("lte", toDate);
        JsonObject timeStampJson = new JsonObject().put("timeStamp", dateRangeJson);
        JsonObject rangeJson = new JsonObject().put("range", timeStampJson);
        JsonObject queryJson = new JsonObject().put("query", rangeJson);
        List<Transaction> transactions = queryTransaction(queryJson, direction, 10000);
        // This only works because the list is sorted by time
        Set<String> nodes = new HashSet<>();
        nodes.add(account);
        List<Transaction> graph = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (graph.size() >= maxGraphLength) {
                break;
            }
            if (direction.equals("desc") && nodes.contains(transaction.getToAccount())) {
                nodes.add(transaction.getFromAccount().getHash());
                graph.add(transaction);
            }
            if (direction.equals("asc") && nodes.contains(transaction.getFromAccount())) {
                nodes.add(transaction.getToAccount().getHash());
                graph.add(transaction);
            }
        }
        return graph;
    }

    /**
     * Base feature to communicate with the low level ES API. This method builds a
     * JSON query like
     * {@code {"query": {"bool": {"should": [{"term": {"<term1>": "<match>"}}, ..]}}} }
     * 
     * @param from
     * @param to
     * @param term
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    public List<Transaction> getFromToTransactions(String from, String to, String term) throws IOException {
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
        return queryTransaction(queryJson, "desc");
    }

    /**
     * Base feature to search the ES database by a document field
     * 
     * @param term
     * @param match
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    private List<Transaction> search(String term, String match) throws IOException {
        return search(term, match, null, null);
    }

    /**
     * Base search method. Uses the low level ES API to match a specific term in the
     * transaction
     * documents that happened between fromDate and toDate
     * 
     * @param term
     * @param match
     * @param fromDate
     * @param toDate
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    private List<Transaction> search(String term, String match, LocalDateTime fromDate, LocalDateTime toDate)
            throws IOException {
        // construct a JSON query like {"query": {"match": {"<term>": "<match>"}}}
        JsonObject queryJson = new JsonObject();
        JsonObject termJson = new JsonObject().put(term, match);
        JsonObject matchJson = new JsonObject().put("match_phrase", termJson);
        if ((fromDate != null) || (toDate != null)) {
            JsonObject dateRangeJson = new JsonObject();
            if (fromDate != null) {
                dateRangeJson.put("gte", fromDate);
            }
            if (toDate != null) {
                dateRangeJson.put("lte", toDate);
            }
            JsonObject timeStampJson = new JsonObject().put("timeStamp", dateRangeJson);
            JsonObject rangeJson = new JsonObject().put("range", timeStampJson);
            JsonArray mustArray = new JsonArray();
            mustArray.add(matchJson);
            mustArray.add(rangeJson);
            JsonObject mustJson = new JsonObject().put("must", mustArray);
            queryJson.put("query", new JsonObject().put("bool", mustJson));
        } else {
            queryJson.put("query", matchJson);
        }
        return queryTransaction(queryJson, "desc", 10000);
    }

    /**
     * Wrapper of a more complete queryTransaction method
     * 
     * @param queryJson
     * @param timeSort
     * @return a list of {@link Transaction}
     * @throws IOException
     */

    private List<Transaction> queryTransaction(JsonObject queryJson, String timeSort) throws IOException {
        return queryTransaction(queryJson, timeSort, 1000);
    }

    /**
     * Most fundamental methods in this class. It ensures that the results are
     * sorted either in
     * ascending or descending (default) order and limits the results size
     * 
     * @param queryJson
     * @param timeSort
     * @param size
     * @return a list of {@link Transaction}
     * @throws IOException
     */
    private List<Transaction> queryTransaction(JsonObject queryJson, String timeSort, int size) throws IOException {
        JsonArray sortTerm = new JsonArray();
        sortTerm.add(new JsonObject().put("timeStamp", timeSort == null ? "desc" : timeSort));
        queryJson.put("sort", sortTerm);
        Request request = new Request("GET", "/transaction/_search?scroll=1m&size=" + size);
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

    /**
     * Parses the ES error and raises an ORIException
     * 
     * @param e
     * @return ORIException
     */
    public ORIException parseElasticError(Exception e) {
        Matcher m = Pattern.compile("status line \\[HTTP/[0-9.]+ ([0-9]+)").matcher(e.getMessage());
        int code = m.find() ? Integer.valueOf(m.group(1)) : 400;
        m = Pattern.compile("status line \\[HTTP/[0-9.]+ [0-9]+ ([A-Za-z ]+)").matcher(e.getMessage());
        String msg = m.find() ? m.group(1) : "";
        return new ORIException(msg, code);
    }
}
