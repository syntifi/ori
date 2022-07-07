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
 * {@link TransferRestAPI} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TransferRestAPITest {

    @Test
    @Order(1)
    public void createChain() throws Exception {
        var chain = new JsonObject();
        chain.put("name", "Chain");
        given()
                .body(chain.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
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
    public void testPostFromAccount() throws Exception {
        var acc = new JsonObject();
        acc.put("hash", "fromacc");
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
                .body("uri", equalTo("/chain/Chain/account/fromacc"));
    }

    @Test
    @Order(4)
    public void testPostToAccount() throws Exception {
        var acc = new JsonObject();
        acc.put("hash", "toacc");
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
                .body("uri", equalTo("/chain/Chain/account/toacc"));
    }

    @Test
    @Order(5)
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
    @Order(6)
    public void testPostTransfer() throws Exception {
        var transfer = new JsonObject();
        transfer.put("amount", 1234);
        transfer.put("tokenSymbol", "ABC");
        transfer.put("hash", "mockTransfer");
        transfer.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        transfer.put("blockHash", "transferBlock");
        transfer.put("fromHash", "fromacc");
        transfer.put("toHash", "toacc");
        given()
                .pathParam("chain", "Chain")
                .body(transfer.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .when()
                .post("/api/v3/chain/{chain}/transfer")
                .then()
                .statusCode(200)
                .body("method", equalTo("POST"))
                .body("uri", equalTo("/chain/Chain/transfer/mockTransfer"));
    }

    @Test
    @Order(7)
    public void testGetTransfer() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("hash", "mockTransfer")
                .when()
                .get("/api/v3/chain/{chain}/transfer/{hash}")
                .then()
                .statusCode(200)
                .body("amount", equalTo(1234F))
                .body("hash", equalTo("mockTransfer"))
                .body("blockHash", equalTo("transferBlock"))
                .body("fromHash", equalTo("fromacc"))
                .body("toHash", equalTo("toacc"))
                .body("timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(8)
    public void testGetIncomingTransferToAccount() {
        given()
                .pathParam("chain", "Chain")
                .queryParam("account", "toacc")
                .queryParam("flow", "IN")
                .when()
                .get("/api/v3/chain/{chain}/transfer")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].amount", equalTo(1234F))
                .body("[0].hash", equalTo("mockTransfer"))
                .body("[0].blockHash", equalTo("transferBlock"))
                .body("[0].fromHash", equalTo("fromacc"))
                .body("[0].toHash", equalTo("toacc"))
                .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(9)
    public void testGetOutgoingTransferFromAccount() {
        given()
                .pathParam("chain", "Chain")
                .queryParam("account", "fromacc")
                .queryParam("flow", "OUT")
                .when()
                .get("/api/v3/chain/{chain}/transfer")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].amount", equalTo(1234F))
                .body("[0].hash", equalTo("mockTransfer"))
                .body("[0].blockHash", equalTo("transferBlock"))
                .body("[0].fromHash", equalTo("fromacc"))
                .body("[0].toHash", equalTo("toacc"))
                .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(10)
    public void testGetTransfers() {
        given()
                .pathParam("chain", "Chain")
                .when()
                .get("/api/v3/chain/{chain}/transfer")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].amount", equalTo(1234F))
                .body("[0].hash", equalTo("mockTransfer"))
                .body("[0].blockHash", equalTo("transferBlock"))
                .body("[0].fromHash", equalTo("fromacc"))
                .body("[0].toHash", equalTo("toacc"))
                .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(11)
    public void testGetNonExistingTransfer() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("hash", "testTransfer")
                .when()
                .get("/api/v3/chain/{chain}/transfer/{hash}")
                .then()
                .statusCode(404)
                .body("error", equalTo("testTransfer not found"));
    }

    @Test
    @Order(12)
    public void testPostMultipleTransfers() throws Exception {
        List<JsonObject> transfers = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            var transfer = new JsonObject();
            transfer.put("amount", 1234);
            transfer.put("tokenSymbol", "ABC");
            transfer.put("hash", "mockTransfer" + ("" + i));
            transfer.put("timeStamp", "2099-08-05T0" + ("" + i) + ":00:00.000+0000");
            transfer.put("blockHash", "transferBlock");
            transfer.put("fromHash", "fromacc");
            transfer.put("toHash", "toacc");
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
    @Order(13)
    public void testDeleteTransfer() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("hash", "mockTransfer")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}/transfer/{hash}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain/transfer/mockTransfer"));
    }

    @Test
    @Order(14)
    public void testDeleteFromAccount() throws InterruptedException {
        given()
                .pathParam("chain", "Chain")
                .pathParam("fromAcc", "fromacc")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}/account/{fromAcc}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain/account/fromacc"));
    }

    @Test
    @Order(15)
    public void testDeleteToAccount() throws InterruptedException {
        given()
                .pathParam("chain", "Chain")
                .pathParam("toAcc", "toacc")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}/account/{toAcc}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain/account/toacc"));
    }

    @Test
    @Order(16)
    public void testDeleteBlock() throws InterruptedException {
        given()
                .pathParam("chain", "Chain")
                .pathParam("hash", "transferBlock")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}/block/{hash}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain/block/transferBlock"));
    }

    @Test
    @Order(17)
    public void testDeleteToken() throws InterruptedException {
        given()
                .pathParam("chain", "Chain")
                .pathParam("token", "ABC")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}/token/{token}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain/token/ABC"));
    }

    @Test
    @Order(18)
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