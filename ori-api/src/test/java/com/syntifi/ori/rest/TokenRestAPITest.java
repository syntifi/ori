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

/**
 * {@link TokenRestAPI} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TokenRestAPITest {

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
                .body(token.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .pathParam("chain", "Chain")
                .when()
                .post("/api/v3/chain/{chain}/token")
                .then()
                .statusCode(200)
                .body("method", equalTo("POST"))
                .body("uri", equalTo("/chain/Chain/token/ABC"));
    }

    @Test
    @Order(3)
    public void testGetToken() {
        given()
                .when()
                .pathParam("chain", "Chain")
                .pathParam("token", "ABC")
                .get("/api/v3/chain/{chain}/token/{token}")
                .then()
                .statusCode(200)
                .body("symbol", equalTo("ABC"))
                .body("name", equalTo("Token ABC"))
                .body("unit", equalTo(1E-18F))
                .body("chainName", equalTo("Chain"));
    }

    @Test
    @Order(4)
    public void testGetTokens() {
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .pathParam("chain", "Chain")
                .when()
                .get("/api/v3/chain/{chain}/token")
                .then()
                .statusCode(200)
                .body("[0].symbol", equalTo("ABC"))
                .body("[0].name", equalTo("Token ABC"))
                .body("[0].unit", equalTo(1E-18F))
                .body("[0].chainName", equalTo("Chain"));
    }

    @Test
    @Order(5)
    public void testGetNonExistingToken() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("token", "EFG")
                .when()
                .get("/api/v3/chain/{chain}/token/{token}")
                .then()
                .statusCode(404)
                .body("error", equalTo("EFG not found"));
    }

    @Test
    @Order(6)
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