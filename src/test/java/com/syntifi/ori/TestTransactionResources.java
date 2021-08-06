package com.syntifi.ori;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.hamcrest.Matchers.containsString;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.HttpHeaders;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TestTransactionResources {

    private static Object LOCK = new Object();

    @Test
    @Order(1)
    public void testPostTransaction() throws Exception  {
        var transaction = new JsonObject();
        transaction.put("amount", 0);
        transaction.put("blockHash", "mockBlockTransaction");
        transaction.put("from", "mockFrom");
        transaction.put("to", "mockTo");
        transaction.put("hash", "mockTransaction");
        transaction.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        given()
          .body(transaction.toString())
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
          .when()
          .post("/transaction")
          .then()
             .statusCode(200)
             .body("created", equalTo("/transaction/mockTransaction"));
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(2)
    public void testGetTransaction() {
        given()
          .when()
          .get("/transaction/mockTransaction")
          .then()
             .statusCode(200)
             .body("amount", equalTo(0F))
             .body("hash", equalTo("mockTransaction"))
             .body("blockHash", equalTo("mockBlockTransaction"))
             .body("from", equalTo("mockFrom"))
             .body("to", equalTo("mockTo"))
             .body("timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(3)
    public void testGetTransactionByAccountUsingFromAccount() {
        given()
          .when()
          .get("/transaction/account/mockFrom")
          .then()
             .statusCode(200)
             .body("size()", equalTo(1))
             .body("[0].amount", equalTo(0F))
             .body("[0].hash", equalTo("mockTransaction"))
             .body("[0].blockHash", equalTo("mockBlockTransaction"))
             .body("[0].from", equalTo("mockFrom"))
             .body("[0].to", equalTo("mockTo"))
             .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(4)
    public void testGetTransactionByAccountUsingToAccount() {
        given()
          .when()
          .get("/transaction/account/mockTo")
          .then()
             .statusCode(200)
             .body("size()", equalTo(1))
             .body("[0].amount", equalTo(0F))
             .body("[0].hash", equalTo("mockTransaction"))
             .body("[0].blockHash", equalTo("mockBlockTransaction"))
             .body("[0].from", equalTo("mockFrom"))
             .body("[0].to", equalTo("mockTo"))
             .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(5)
    public void testGetTransactions() {
        given()
          .when()
          .get("/transaction")
          .then()
             .statusCode(200)
             .body("[0].amount", equalTo(0F))
             .body("[0].hash", equalTo("mockTransaction"))
             .body("[0].blockHash", equalTo("mockBlockTransaction"))
             .body("[0].from", equalTo("mockFrom"))
             .body("[0].to", equalTo("mockTo"))
             .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"));
    }

    @Test
    @Order(6)
    public void testGetNonExistingTransaction() {
        given()
          .when()
          .get("/transaction/testTransaction")
          .then()
             .statusCode(404)
             .body("error", equalTo("Not Found"));
    }

    @Test
    @Order(7)
    public void testPostTransactionWrongDateType() {
        var transaction = new JsonObject();
        transaction.put("amount", 0F);
        transaction.put("blockHash", "mockBlockTransaction");
        transaction.put("from", "mockFrom");
        transaction.put("to", "mockTo");
        transaction.put("hash", "mockTransaction");
        transaction.put("timeStamp", "2099-08-05");
        given()
          .body(transaction.toString())
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
          .when()
          .post("/transaction")
          .then()
             .statusCode(400)
             .body("error", containsString("Date"));
    }

    @Test
    @Order(8)
    public void testPostTransactionWithoutHash() {
        var transaction = new JsonObject();
        transaction.put("amount", 0F);
        transaction.put("blockHash", "mockBlockTransaction");
        transaction.put("from", "mockFrom");
        transaction.put("to", "mockTo");
        transaction.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        given()
          .body(transaction.toString())
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
          .when()
          .post("/transaction")
          .then()
             .statusCode(404)
             .body("error", equalTo("Transaction hash missing"));
    }

    @Test
    @Order(9)
    public void testDeleteTransaction() {
        given()
            .when()
            .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
            .delete("/transaction/mockTransaction")
            .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/transaction/mockTransaction"));
    }


}