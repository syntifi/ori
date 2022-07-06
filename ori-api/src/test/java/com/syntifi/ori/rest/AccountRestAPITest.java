package com.syntifi.ori.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * {@link AccountRestAPI} tests
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class AccountRestAPITest {


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
    public void testPostAccount() throws Exception {
        var block = new JsonObject();
        block.put("hash", "mockAccount");
        block.put("publicKey", "key");
        block.put("label", "label");
        given()
                .body(block.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .pathParam("chain", "Chain")
                .when()
                .post("/api/v3/chain/{chain}/account")
                .then()
                .statusCode(200)
                .body("method", equalTo("POST"))
                .body("uri", equalTo("/chain/Chain/account/mockAccount"));
    }

    @Test
    @Order(3)
    public void testGetAccount() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "mockAccount")
                .when()
                .get("/api/v3/chain/{chain}/account/{account}")
                .then()
                .statusCode(200)
                .body("chainName", equalTo("Chain"))
                .body("hash", equalTo("mockAccount"))
                .body("publicKey", equalTo("key"))
                .body("label", equalTo("label"));
    }

    @Test
    @Order(4)
    public void testGetAccounts() {
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .pathParam("chain", "Chain")
                .when()
                .get("/api/v3/chain/{chain}/account")
                .then()
                .statusCode(200)
                .body("[0].chainName", equalTo("Chain"))
                .body("[0].hash", equalTo("mockAccount"))
                .body("[0].publicKey", equalTo("key"))
                .body("[0].label", equalTo("label"));
    }

    @Test
    @Order(5)
    public void testGetNonExistingAccount() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "noAccount")
                .when()
                .get("/api/v3/chain/{chain}/account/{account}")
                .then()
                .statusCode(404)
                .body("error", equalTo("noAccount not found"));
    }

    @Test
    @Order(6)
    public void testDeleteAccount() throws InterruptedException {
        given()
                .pathParam("chain", "Chain")
                .pathParam("account", "mockAccount")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}/account/{account}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain/account/mockAccount"));
    }

    @Test
    @Order(7)
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