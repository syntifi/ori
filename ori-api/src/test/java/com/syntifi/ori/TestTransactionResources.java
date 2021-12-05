package com.syntifi.ori;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;

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
          .post("/api/v1/transaction")
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
          .get("/api/v1/transaction/mockTransaction")
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
          .get("/api/v1/transaction/account/mockFrom")
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
          .get("/api/v1/transaction/account/mockTo")
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
          .get("/api/v1/transaction")
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
    public void testGetTransactionFilteredByFromAccount() {
        given()
          .param("fromAccount", "mockFrom")
            .when()
            .get("/api/v1/transaction")
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
    @Order(7)
    public void testGetTransactionFilteredByToAccount() {
        given()
          .param("toAccount", "mockTo")
            .when()
            .get("/api/v1/transaction")
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
    @Order(8)
    public void testGetNonExistingTransaction() {
        given()
          .when()
          .get("/api/v1/transaction/testTransaction")
          .then()
             .statusCode(404)
             .body("error", equalTo("Not Found"));
    }

    @Test
    @Order(9)
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
          .post("/api/v1/transaction")
          .then()
             .statusCode(400)
             .body("error", containsString("Date"));
    }

    @Test
    @Order(10)
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
          .post("/api/v1/transaction")
          .then()
             .statusCode(404)
             .body("error", equalTo("Transaction hash missing"));
    }

    @Test
    @Order(11)
    public void testDeleteTransaction() {
        given()
            .when()
            .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
            .delete("/api/v1/transaction/mockTransaction")
            .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/transaction/mockTransaction"));
    }


}