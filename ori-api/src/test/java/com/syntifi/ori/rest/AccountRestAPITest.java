package com.syntifi.ori.rest;

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
public class AccountRestAPITest {
    
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
    public void testPostAccount() throws Exception {
        var block = new JsonObject();
        block.put("hash", "mockAccount");
        block.put("publicKey", "key");
        block.put("label", "label");
        given()
                .body(block.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v2/account/ABC")
                .then()
                .statusCode(200)
                .body("created", equalTo("/account/ABC/mockAccount"));
    }

    @Test
    @Order(3)
    public void testGetAccount() {
        given()
                .when()
                .get("/api/v2/account/ABC/hash/mockAccount")
                .then()
                .statusCode(200)
                .body("tokenSymbol", equalTo("ABC"))
                .body("hash", equalTo("mockAccount"))
                .body("publicKey", equalTo("key"))
                .body("label", equalTo("label"));
    }

    @Test
    @Order(4)
    public void testGetAccounts() {
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .get("/api/v2/account/ABC")
                .then()
                .statusCode(200)
                .body("[0].tokenSymbol", equalTo("ABC"))
                .body("[0].hash", equalTo("mockAccount"))
                .body("[0].publicKey", equalTo("key"))
                .body("[0].label", equalTo("label"));
    }

    @Test
    @Order(5)
    public void testGetNonExistingAccount() {
        given()
                .when()
                .get("/api/v2/account/ABC/hash/noAccount")
                .then()
                .statusCode(404)
                .body("error", equalTo("noAccount not found"));
    }

    @Test
    @Order(6)
    public void testDeleteAccount() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/account/ABC/hash/mockAccount")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/account/ABC/hash/mockAccount"));
    }

    @Test
    @Order(7)
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