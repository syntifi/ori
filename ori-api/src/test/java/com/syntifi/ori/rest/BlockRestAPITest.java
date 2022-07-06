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
 * {@link BlockRestAPI} tests
 * 
 * @author Alexandre Carvalho
 * @author Andre Bertolace
 * 
 * @since 0.1.0
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class BlockRestAPITest {

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
    public void testPostBlock() throws Exception {
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        block.put("validator", "validator");
        given()
                .body(block.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .pathParam("chain", "Chain")
                .when()
                .post("/api/v3/chain/{chain}/block")
                .then()
                .statusCode(200)
                .body("method", equalTo("POST"))
                .body("uri", equalTo("/chain/Chain/block/mockBlock"));
    }

    @Test
    @Order(3)
    public void testGetBlock() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("block", "mockBlock")
                .when()
                .get("/api/v3/chain/{chain}/block/{block}")
                .then()
                .statusCode(200)
                .body("chainName", equalTo("Chain"))
                .body("era", equalTo(0))
                .body("hash", equalTo("mockBlock"))
                .body("height", equalTo(0))
                .body("parent", equalTo(null))
                .body("root", equalTo("root"))
                .body("timeStamp", equalTo("2099-08-05T00:00:00.000+0000"))
                .body("validator", equalTo("validator"));
    }

    @Test
    @Order(4)
    public void testGetBlocks() {
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .pathParam("chain", "Chain")
                .when()
                .get("/api/v3/chain/{chain}/block")
                .then()
                .statusCode(200)
                .body("[0].chainName", equalTo("Chain"))
                .body("[0].era", equalTo(0))
                .body("[0].hash", equalTo("mockBlock"))
                .body("[0].height", equalTo(0))
                .body("[0].parent", equalTo(null))
                .body("[0].root", equalTo("root"))
                .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"))
                .body("[0].validator", equalTo("validator"));
    }

    @Test
    @Order(5)
    public void testGetLastBlock() {
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .pathParam("chain", "Chain")
                .when()
                .get("/api/v3/chain/{chain}/block/last")
                .then()
                .statusCode(200)
                .body("chainName", equalTo("Chain"))
                .body("era", equalTo(0))
                .body("hash", equalTo("mockBlock"))
                .body("height", equalTo(0))
                .body("parent", equalTo(null))
                .body("root", equalTo("root"))
                .body("timeStamp", equalTo("2099-08-05T00:00:00.000+0000"))
                .body("validator", equalTo("validator"));
    }

    @Test
    @Order(6)
    public void testGetNonExistingBlock() {
        given()
                .pathParam("chain", "Chain")
                .pathParam("block", "noBlock")
                .when()
                .get("/api/v3/chain/{chain}/block/{block}")
                .then()
                .statusCode(404)
                .body("error", equalTo("noBlock not found"));
    }

    @Test
    @Order(7)
    public void testPostAnotherBlock() throws Exception {
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "mockBlock2");
        block.put("height", 0);
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        block.put("validator", "validator");
        block.put("parent", "mockBlock");
        given()
                .body(block.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .pathParam("chain", "Chain")
                .when()
                .post("/api/v3/chain/{chain}/block")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(8)
    public void testPostMultipleBlocks() throws Exception {
        List<JsonObject> blocks = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            var block = new JsonObject();
            block.put("era", 0);
            block.put("hash", "multipleBlocks" + ("" + i));
            block.put("height", i);
            block.put("root", "root");
            block.put("timeStamp", "2099-01-01T0" + ("" + i) + ":00:00.000+0000");
            block.put("parent", i == 0 ? "mockBlock2" : "multipleBlocks" + ("" + (i - 1)));
            block.put("validator", "validator");
            blocks.add(block);
        }
        given()
                .body(blocks.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .pathParam("chain", "Chain")
                .when()
                .post("/api/v3/chain/{chain}/block/multiple")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(9)
    public void testDeleteBlock() throws InterruptedException {
        given()
                .pathParam("chain", "Chain")
                .pathParam("block", "mockBlock2")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}/block/{block}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain/block/mockBlock2"));
    }

    @Test
    @Order(10)
    public void testDeleteBlock2() throws InterruptedException {
        given()
                .pathParam("chain", "Chain")
                .pathParam("block", "mockBlock")
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v3/chain/{chain}/block/{block}")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/chain/Chain/block/mockBlock"));
    }

    @Test
    @Order(11)
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