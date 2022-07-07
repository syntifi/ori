package com.syntifi.ori.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;

/**
 * {@link TransferMonitorAPI} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TransferMonitorRestAPITest {

    @Test
    @Order(1)
    public void createChain() throws Exception {
        var chain = new JsonObject();
        chain.put("name", "Chain");
        given()
                .body(chain.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v3/chain")
                .then()
                .statusCode(200)
                .body("method", equalTo("POST"))
                .body("uri", equalTo("/chain/Chain"));
    }

    @Test
    @Order(2)
    public void createToken() throws Exception {
        var token = new JsonObject();
        token.put("symbol", "ABC");
        token.put("name", "Token ABC");
        token.put("unit", 1E-18);
        given()
                .pathParam("chain", "Chain")
                .body(token.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .when()
                .post("/api/v3/chain/{chain}/token")
                .then()
                .statusCode(200)
                .body("method", equalTo("POST"))
                .body("uri", equalTo("/chain/Chain/token/ABC"));
    }

    @Test
    @Order(3)
    public void testPostAccounts() throws Exception {
        String[] accounts = { "A", "B", "C" };
        for (String account : accounts) {
            var acc = new JsonObject();
            acc.put("hash", account);
            acc.put("publicKey", "key");
            acc.put("label", "label");
            given()
                    .pathParam("chain", "Chain")
                    .body(acc.toString())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .when()
                    .post("/api/v3/chain/{chain}/account")
                    .then()
                    .statusCode(200)
                    .body("method", equalTo("POST"))
                    .body("uri", equalTo("/chain/Chain/account/"+account));
        }
    }

    @Test
    @Order(4)
    public void testPostBlock() throws Exception {
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "transferBlock");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        block.put("validator", "validator");
        given()
                .pathParam("chain", "Chain")
                .body(block.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v3/chain/{chain}/block")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(5)
    public void testPostMultipleTransfers() throws Exception {
        List<JsonObject> transfers = new LinkedList<>();
        String[] from = { "A", "A", "B", "A", "A", "A", "A", "A", "A", "B" };
        String[] to = { "B", "B", "C", "C", "C", "C", "B", "B", "B", "C" };
        for (int i = 0; i < 10; i++) {
            var transfer = new JsonObject();
            transfer.put("amount", 1234);
            transfer.put("tokenSymbol", "ABC");
            transfer.put("hash", "mockTransfer" + ("" + i));
            transfer.put("timeStamp", "2099-08-05T0" + ("" + i) + ":00:00.000+0000");
            transfer.put("blockHash", "transferBlock");
            transfer.put("fromHash", from[i]);
            transfer.put("toHash", to[i]);
            transfers.add(transfer);
        }
        given()
                .pathParam("chain", "Chain")
                .body(transfers.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v3/chain/{chain}/transfer/multiple")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(6)
    public void testBackTraceTheCoin() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "B")
                .when()
                .get("/api/v3/chain/{chain}/monitor/trace/back/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @Order(7)
    public void testBackTraceTheCoinFilterToDate() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "B")
                .queryParam("toDate", "2099-08-05T04:00:00.000+0000")
                .when()
                .get("/api/v3/chain/{chain}/monitor/trace/back/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @Test
    @Order(8)
    public void testBackTraceTheCoinFilterFromAndToDate() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "B")
                .queryParam("toDate", "2099-08-05T04:00:00.000+0000")
                .queryParam("fromDate", "2099-08-05T00:30:00.000+0000")
                .when()
                .get("/api/v3/chain/{chain}/monitor/trace/back/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    @Order(9)
    public void testForwardTraceTheCoin() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "B")
                .when()
                .get("/api/v3/chain/{chain}/monitor/trace/forward/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @Order(10)
    public void testForwardTraceTheCoinFilterToDate() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "B")
                .queryParam("fromDate", "2099-08-05T00:00:00.000+0000")
                .when()
                .get("/api/v3/chain/{chain}/monitor/trace/forward/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @Test
    @Order(11)
    public void testForwardTraceTheCoinFilterFromAndToDate() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "B")
                .queryParam("fromDate", "2099-08-05T04:00:00.000+0000")
                .queryParam("toDate", "2099-08-06T00:00:00.000+0000")
                .when()
                .get("/api/v3/chain/{chain}/monitor/trace/forward/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    @Order(12)
    public void testDeleteChain() throws InterruptedException {
        given()
                .pathParam("chain", "Chain")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain"));
    }
}