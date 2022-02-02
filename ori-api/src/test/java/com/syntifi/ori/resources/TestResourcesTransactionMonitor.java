package com.syntifi.ori.resources;

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

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TestResourcesTransactionMonitor {

    private static Object LOCK = new Object();

    @Test
    @Order(1)
    public void createToken() throws Exception {
        var token = new JsonObject();
        token.put("symbol", "ABC");
        token.put("name", "Token ABC");
        token.put("protocol", "A");
        given()
                .body(token.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .when()
                .post("/api/v2/token")
                .then()
                .statusCode(200)
                .body("created", equalTo("/token/ABC"));
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(2)
    public void testPostAccounts() throws Exception {
        String[] accounts = { "A", "B", "C" };
        for (String account : accounts) {
            var acc = new JsonObject();
            acc.put("hash", account);
            acc.put("publicKey", "key");
            acc.put("label", "label");
            given()
                    .pathParam("token", "ABC")
                    .body(acc.toString())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .when()
                    .post("/api/v2/account/{token}")
                    .then()
                    .statusCode(200)
                    .body("created", equalTo("/account/ABC/" + account));
        }
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(4)
    public void testPostBlock() throws Exception {
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "transactionBlock");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        block.put("validator", "validator");
        given()
                .pathParam("token", "ABC")
                .body(block.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v2/block/{token}")
                .then()
                .statusCode(201);
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(5)
    public void testPostMultipleTransactions() throws Exception {
        List<JsonObject> transactions = new LinkedList<>();
        String[] from = { "A", "A", "B", "A", "A", "A", "A", "A", "A", "B" };
        String[] to = { "B", "B", "C", "C", "C", "C", "B", "B", "B", "C" };
        for (int i = 0; i < 10; i++) {
            var transaction = new JsonObject();
            transaction.put("amount", 1234);
            transaction.put("hash", "mockTransaction" + ("" + i));
            transaction.put("timeStamp", "2099-08-05T0" + ("" + i) + ":00:00.000+0000");
            transaction.put("blockHash", "transactionBlock");
            transaction.put("fromHash", from[i]);
            transaction.put("toHash", to[i]);
            transactions.add(transaction);
        }
        given()
                .pathParam("token", "ABC")
                .body(transactions.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v2/transaction/{token}/multiple")
                .then()
                .statusCode(201);
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(6)
    public void testBackTraceTheCoin() {
        given()
                .pathParam("symbol", "ABC")
                .pathParam("account", "B")
                .when()
                .get("/api/v2/monitor/{symbol}/traceCoin/back/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @Order(7)
    public void testBackTraceTheCoinFilterToDate() {
        given()
                .pathParam("symbol", "ABC")
                .pathParam("account", "B")
                .queryParam("toDate", "2099-08-05T04:00:00.000")
                .when()
                .get("/api/v2/monitor/{symbol}/traceCoin/back/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @Test
    @Order(8)
    public void testBackTraceTheCoinFilterFromAndToDate() {
        given()
                .pathParam("symbol", "ABC")
                .pathParam("account", "B")
                .queryParam("toDate", "2099-08-05T04:00:00.000")
                .queryParam("fromDate", "2099-08-05T00:30:00.000")
                .when()
                .get("/api/v2/monitor/{symbol}/traceCoin/back/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    @Order(9)
    public void testDeleteToken() throws InterruptedException {
        given()
                .pathParam("symbol", "ABC")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/token/{symbol}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/token/ABC"));
    }
}