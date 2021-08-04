package com.syntifi.ori.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.syntifi.ori.model.Transaction;
import com.syntifi.ori.exception.ORIException;

import org.jboss.logging.Logger;

@ApplicationScoped
public class TransactionService {
    private static final Logger LOG = Logger.getLogger(TransactionService.class);
    private int maxGraphLength = ConfigProvider.getConfig().getValue("ori.aml.max-trace-coin-length", int.class);

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
        return queryTransaction(queryJson, "desc");
    }

    public List<Transaction> getOutgoingTransactions(String from) throws IOException {
        return search("from", from);
    }

    public List<Transaction> getOutgoingTransactions(String from, LocalDateTime fromDate, LocalDateTime toDate) throws IOException {
        return search("from", from, fromDate, toDate);
    }

    public List<Transaction> getIncomingTransactions(String to) throws IOException {
        return search("to", to);
    }

    public List<Transaction> getIncomingTransactions(String to, LocalDateTime fromDate, LocalDateTime toDate) throws IOException {
        return search("to", to, fromDate, toDate);
    }

    public List<Transaction> getAllTransactionsByAccount(String account) throws IOException {
        return getFromToTransactions(account, account, "should");
    }

    public List<Transaction> getTransactionsFromAccountToAccount(String from, String to) throws IOException {
        return getFromToTransactions(from, to, "must");
    }

    public List<Transaction> reverseGraphWalk(String account, LocalDateTime fromDate, LocalDateTime toDate) throws IOException {
        return graphWalk(account, fromDate, toDate, "desc");
    }

    public List<Transaction> forwardGraphWalk(String account, LocalDateTime fromDate, LocalDateTime toDate) throws IOException {
        return graphWalk(account, fromDate, toDate, "asc");
    }

    public List<Transaction> graphWalk(String account, LocalDateTime fromDate, LocalDateTime toDate, String direction) throws IOException {
        // construct a Json query like   "query": { "range": {  "timestamp": {
        //     "gte": <fromDate>,  "lte": <toDate>  } } } 
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
        for (Transaction transaction: transactions){
            if (graph.size() >= maxGraphLength) {
                break;
            }
            if (direction.equals("desc") && nodes.contains(transaction.to)) {
                nodes.add(transaction.from);
                graph.add(transaction);
            }
            if (direction.equals("asc") && nodes.contains(transaction.from)) {
                nodes.add(transaction.to);
                graph.add(transaction);
            }
        }
        return graph;
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
        return queryTransaction(queryJson, "desc");
    }


    private List<Transaction> search(String term, String match) throws IOException {
        return search(term, match, null, null);
    }

    private List<Transaction> search(String term, String match, LocalDateTime fromDate, LocalDateTime toDate) throws IOException {
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
        LOG.info(queryJson.toString());
        return queryTransaction(queryJson, "desc", 10000);
    }

    private List<Transaction> queryTransaction(JsonObject queryJson, String timeSort) throws IOException {
        return queryTransaction(queryJson, timeSort, 1000);
    }

    private List<Transaction> queryTransaction(JsonObject queryJson, String timeSort, int size) throws IOException {
        JsonArray sortTerm = new JsonArray();
        sortTerm.add(new JsonObject().put("timeStamp", timeSort==null ? "desc" : timeSort));
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

    public ORIException parseElasticError(Exception e){
            Matcher m = Pattern.compile("status line [HTTP/[0-9.]+ ([0-9]+) [A-Za-z ]+]").matcher(e.getMessage());
            int code = m.matches() ? Integer.valueOf(m.group(1)) : 404;
            m = Pattern.compile("status line [HTTP/[0-9.]+ ([0-9]+) [A-Za-z ]+]").matcher(e.getMessage());
            String msg = m.matches() ? m.group(1) : "";
            return new ORIException(msg, code);
    }
}
