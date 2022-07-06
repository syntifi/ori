package com.syntifi.ori.rest;


import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * {@link ChainRestAPI} tests
 *
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChainRestAPITest {

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
    public void testGetChain() {
        given()
                .pathParam("chain", "Chain")
                .when()
                .get("/api/v3/chain/{chain}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Chain"));
    }

    @Test
    @Order(3)
    public void testGetChains() {
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .get("/api/v3/chain")
                .then()
                .statusCode(200)
                .body("[0].name", equalTo("Chain"));
    }

    @Test
    @Order(4)
    public void testGetNonExistingChain() {
        given()
                .pathParam("chain", "NoChain")
                .when()
                .get("/api/v3/chain/{chain}")
                .then()
                .statusCode(404)
                .body("error", equalTo("NoChain not found"));
    }

    @Test
    @Order(5)
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
