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
 * {@link TransactionRestAPI} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TransactionRestAPITest {

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
    }

    @Test
    @Order(2)
    public void testPostFromAccount() throws Exception {
        var acc = new JsonObject();
        acc.put("hash", "fromacc");
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
                .body("created", equalTo("/account/ABC/fromacc"));
    }

    @Test
    @Order(3)
    public void testPostToAccount() throws Exception {
        var acc = new JsonObject();
        acc.put("hash", "toacc");
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
                .body("created", equalTo("/account/ABC/toacc"));
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
    }

    @Test
    @Order(5)
    public void testPostTransaction() throws Exception {
        var transaction = new JsonObject();
        transaction.put("amount", 1234);
        transaction.put("hash", "mockTransaction");
        transaction.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        transaction.put("blockHash", "transactionBlock");
        transaction.put("fromHash", "fromacc");
        transaction.put("toHash", "toacc");
        given()
                .pathParam("token", "ABC")
                .body(transaction.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .when()
                .post("/api/v2/transaction/{token}")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(6)
    public void testGetTransaction() {
        given()
                .pathParam("token", "ABC")
                .pathParam("hash", "mockTransaction")
                .when()
                .get("/api/v2/transaction/{token}/hash/{hash}")
                .then()
                .statusCode(200)
                .body("amount", equalTo(1234F))
                .body("hash", equalTo("mockTransaction"))
                .body("blockHash", equalTo("transactionBlock"))
                .body("fromHash", equalTo("fromacc"))
                .body("toHash", equalTo("toacc"))
                .body("timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(7)
    public void testGetIncomingTransactionToAccount() {
        given()
                .pathParam("token", "ABC")
                .pathParam("account", "toacc")
                .when()
                .get("/api/v2/transaction/{token}/incoming/account/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].amount", equalTo(1234F))
                .body("[0].hash", equalTo("mockTransaction"))
                .body("[0].blockHash", equalTo("transactionBlock"))
                .body("[0].fromHash", equalTo("fromacc"))
                .body("[0].toHash", equalTo("toacc"))
                .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(8)
    public void testGetOutgoingTransactionFromAccount() {
        given()
                .pathParam("token", "ABC")
                .pathParam("account", "fromacc")
                .when()
                .get("/api/v2/transaction/{token}/outgoing/account/{account}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].amount", equalTo(1234F))
                .body("[0].hash", equalTo("mockTransaction"))
                .body("[0].blockHash", equalTo("transactionBlock"))
                .body("[0].fromHash", equalTo("fromacc"))
                .body("[0].toHash", equalTo("toacc"))
                .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(9)
    public void testGetTransactions() {
        given()
                .pathParam("token", "ABC")
                .when()
                .get("/api/v2/transaction/{token}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].amount", equalTo(1234F))
                .body("[0].hash", equalTo("mockTransaction"))
                .body("[0].blockHash", equalTo("transactionBlock"))
                .body("[0].fromHash", equalTo("fromacc"))
                .body("[0].toHash", equalTo("toacc"))
                .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(10)
    public void testGetNonExistingTransaction() {
        given()
                .pathParam("token", "ABC")
                .pathParam("hash", "testTransaction")
                .when()
                .get("/api/v2/transaction/{token}/hash/{hash}")
                .then()
                .statusCode(404)
                .body("error", equalTo("testTransaction not found"));
    }

    @Test
    @Order(11)
    public void testPostMultipleTransactions() throws Exception {
        List<JsonObject> transactions = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            var transaction = new JsonObject();
            transaction.put("amount", 1234);
            transaction.put("hash", "mockTransaction" + ("" + i));
            transaction.put("timeStamp", "2099-08-05T0" + ("" + i) + ":00:00.000+0000");
            transaction.put("blockHash", "transactionBlock");
            transaction.put("fromHash", "fromacc");
            transaction.put("toHash", "toacc");
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
    }

    @Test
    @Order(12)
    public void testDeleteTransaction() {
        given()
                .pathParam("token", "ABC")
                .pathParam("hash", "mockTransaction")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/transaction/{token}/hash/{hash}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/transaction/ABC/hash/mockTransaction"));
    }

    @Test
    @Order(13)
    public void testDeleteFromAccount() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/account/ABC/hash/fromacc")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/account/ABC/hash/fromacc"));
    }

    @Test
    @Order(14)
    public void testDeleteToAccount() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/account/ABC/hash/toacc")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/account/ABC/hash/toacc"));
    }

    @Test
    @Order(15)
    public void testDeleteBlock() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/block/ABC/hash/transactionBlock")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/block/ABC/hash/transactionBlock"));
    }

    @Test
    @Order(16)
    public void testDeleteToken() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/token/ABC")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/token/ABC"));
    }
}