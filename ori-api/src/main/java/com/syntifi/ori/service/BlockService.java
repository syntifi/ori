package com.syntifi.ori.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.syntifi.ori.exception.ORIException;
import com.syntifi.ori.model.Block;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * All block related services for querying ES using the low level API 
 * 
 * @author Andre Bertolace 
 * @since 0.1.0
 */
@ApplicationScoped
public class BlockService {

    @Inject
    RestClient restClient;

    /**
     * Indexes a new block document on Elastic Search 
     * 
     * @param block
     * @throws IOException
     */
    public void index(Block block) throws IOException {
        Request request = new Request("PUT", "/block/_doc/" + block.getHash());
        request.setJsonEntity(JsonObject.mapFrom(block).toString());
        restClient.performRequest(request);
    }

    /**
     * Removes all block documents indexed in ES
     * 
     * @return ES Response
     * @throws IOException
     */
    public Response clear() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        Request request = new Request("POST", "/block/_delete_by_query?conflicts=proceed");
        request.setJsonEntity(queryJson.encode());
        return restClient.performRequest(request);
    }

    /**
     * Deletes a block given its hash
     * 
     * @param hash
     * @return ES Response
     * @throws IOException
     */
    public Response delete(String hash) throws IOException {
        Request request = new Request("DELETE", "/block/_doc/" + hash);
        return restClient.performRequest(request);
    }

    /**
     * Retrieves a block given its hash
     * 
     * @param hash
     * @return Block
     * @throws IOException
     */
    public Block getBlockByHash(String hash) throws IOException {
        Request request = new Request("GET", "/block/_doc/" + hash);
        Response response = restClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject json = new JsonObject(responseBody);
        return json.getJsonObject("_source").mapTo(Block.class);
    }

    /**
     * Retrieves all blocks indexed in the database sorted by date in descending order
     * 
     * @return List<Block>
     * @throws IOException
     */

    public List<Block> getAllBlocks() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        return queryBlock(queryJson, true);
    }

    /**
     * Returns the last block in the database 
     * 
     * @return Block
     * @throws IOException
     */
    public Block getLastBlock() throws IOException {
        JsonObject matchJson = new JsonObject().put("match_all", new JsonObject());
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        queryJson.put("size", 1);
        return queryBlock(queryJson, true).get(0);
    }

    /**
     * The most fundamental methods in this class. It ensures that the results are sorted 
     * descending order 
     * 
     * @param queryJson
     * @param sortByDate
     * @return List<Block>
     * @throws IOException
     */
    private List<Block> queryBlock(JsonObject queryJson, boolean sortByDate) throws IOException {
        if (sortByDate) {
            JsonArray sortTerm = new JsonArray();
            sortTerm.add(new JsonObject().put("timeStamp", "desc"));
            queryJson.put("sort", sortTerm);
        }
        Request request = new Request("GET", "/block/_search?scroll=1m&size=10");
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

    /**
     * Parses the ES error and raises an ORIException
     * 
     * @param e
     * @return ORIException
     */
    public ORIException parseElasticError(Exception e){
        Matcher m = Pattern.compile("status line \\[HTTP/[0-9.]+ ([0-9]+)").matcher(e.getMessage());
        int code = m.find() ? Integer.valueOf(m.group(1)) : 404;
        m = Pattern.compile("status line \\[HTTP/[0-9.]+ [0-9]+ ([A-Za-z ]+)").matcher(e.getMessage());
        String msg = m.find() ? m.group(1) : "";
        return new ORIException(msg, code);
    }
}
