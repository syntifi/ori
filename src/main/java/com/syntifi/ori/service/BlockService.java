package com.syntifi.ori.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Block;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BlockService {

    @Inject
    RestClient restClient;

    public void index(Block block) throws IOException {
        Request request = new Request("PUT", "/block/_doc/" + block.hash);
        request.setJsonEntity(JsonObject.mapFrom(block).toString());
        restClient.performRequest(request);
    }

    public Response clear() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        Request request = new Request("POST", "/block/_delete_by_query?conflicts=proceed");
        request.setJsonEntity(queryJson.encode());
        return restClient.performRequest(request);
    }

    public Response delete(String hash) throws IOException {
        Request request = new Request("DELETE", "/block/" + hash);
        return restClient.performRequest(request);
    }

    public Block getBlockByHash(String hash) throws IOException {
        Request request = new Request("GET", "/block/_doc/" + hash);
        Response response = restClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject json = new JsonObject(responseBody);
        return json.getJsonObject("_source").mapTo(Block.class);
    }

    public List<Block> getAllBlocks() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        return queryBlock(queryJson, true);
    }

    public Block getLastBlock() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        queryJson.put("size", 1);
        return queryBlock(queryJson, true).get(0);
    }

    private List<Block> queryBlock(JsonObject queryJson, boolean sortByDate) throws IOException {
        if (sortByDate) {
            JsonArray sortTerm = new JsonArray();
            sortTerm.add(new JsonObject().put("timeStamp", "desc"));
            queryJson.put("sort", sortTerm);
        }
        Request request = new Request("GET", "/block/_search?scroll=1m&size=100");
        request.setJsonEntity(queryJson.encode());
        Response response = restClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject json = new JsonObject(responseBody);
        long total = json.getJsonObject("hits").getJsonObject("total").getLong("value");
        List<Block> results = new ArrayList<>();
        JsonArray hits = json.getJsonObject("hits").getJsonArray("hits");
        for (int i = 0; i < hits.size(); i++) {
            JsonObject hit = hits.getJsonObject(i);
            Block block = hit.getJsonObject("_source").mapTo(Block.class);
            results.add(block);
        }
        return results;
    }

    public ORIException parseElasticError(Exception e){
        Matcher m = Pattern.compile("status line \\[HTTP/[0-9.]+ ([0-9]+)").matcher(e.getMessage());
        int code = m.find() ? Integer.valueOf(m.group(1)) : 404;
        m = Pattern.compile("status line \\[HTTP/[0-9.]+ [0-9]+ ([A-Za-z ]+)").matcher(e.getMessage());
        String msg = m.find() ? m.group(1) : "";
        return new ORIException(msg, code);
    }
}
