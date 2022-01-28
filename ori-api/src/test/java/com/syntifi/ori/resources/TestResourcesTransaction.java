package com.syntifi.ori.resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

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
public class TestResourcesTransaction {

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
    public void testPostFromAccount() throws Exception {
        var acc = new JsonObject();
        acc.put("hash", "fromacc");
        acc.put("publicKey", "key");
        acc.put("label", "label");
        given()
                .body(acc.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v2/account/ABC")
                .then()
                .statusCode(200)
                .body("created", equalTo("/account/ABC/fromacc"));
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(3)
    public void testPostToAccount() throws Exception {
        var acc = new JsonObject();
        acc.put("hash", "toacc");
        acc.put("publicKey", "key");
        acc.put("label", "label");
        given()
                .body(acc.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v2/account/ABC")
                .then()
                .statusCode(200)
                .body("created", equalTo("/account/ABC/toacc"));
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
        block.put("parent", "null");
        given()
                .body(block.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v2/block/ABC")
                .then()
                .statusCode(201);
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
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
                .body(transaction.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .when()
                .post("/api/v2/transaction/ABC")
                .then()
                .statusCode(201);
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(6)
    public void testGetTransaction() {
        given()
                .when()
                .get("/api/v2/transaction/ABC/hash/mockTransaction")
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
                .when()
                .get("/api/v2/transaction/ABC/incoming/account/toacc")
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
                .when()
                .get("/api/v2/transaction/ABC/outgoing/account/fromacc")
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
    public void testGetTransactions() {
        given()
                .when()
                .get("/api/v2/transaction/ABC")
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
    public void testGetNonExistingTransaction() {
        given()
                .when()
                .get("/api/v2/transaction/ABC/hash/testTransaction")
                .then()
                .statusCode(404)
                .body("error", equalTo("testTransaction not found"));
    }

    @Test
    @Order(9)
    public void testDeleteTransaction() {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/transaction/ABC/hash/mockTransaction")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/transaction/ABC/hash/mockTransaction"));
    }

    @Test
    @Order(10)
    public void testDeleteFromAccount() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/account/ABC/hash/fromacc")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/account/ABC/hash/fromacc"));
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(11)
    public void testDeleteToAccount() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/account/ABC/hash/toacc")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/account/ABC/hash/toacc"));
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(12)
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
    @Order(13)
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