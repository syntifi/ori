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
public class TestResoucesBlock {

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
                .when()
                .post("/api/v2/block/ABC/parent/null")
                .then()
                .statusCode(200)
                .body("created", equalTo("/block/ABC/hash/mockBlock"));
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(3)
    public void testGetBlock() {
        given()
                .when()
                .get("/api/v2/block/ABC/hash/mockBlock")
                .then()
                .statusCode(200)
                .body("tokenSymbol", equalTo("ABC"))
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
                .when()
                .get("/api/v2/block/ABC")
                .then()
                .statusCode(200)
                .body("[0].tokenSymbol", equalTo("ABC"))
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
                .when()
                .get("/api/v2/block/ABC/last")
                .then()
                .statusCode(200)
                .body("tokenSymbol", equalTo("ABC"))
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
                .when()
                .get("/api/v2/block/ABC/hash/noBlock")
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
        given()
                .body(block.toString())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v2/block/ABC/parent/mockBlock")
                .then()
                .statusCode(200)
                .body("created", equalTo("/block/ABC/hash/mockBlock2"));
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(8)
    public void testDeleteBlock() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/block/ABC/hash/mockBlock2")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/block/ABC/hash/mockBlock2"));
    }

    @Test
    @Order(9)
    public void testDeleteBlock2() throws InterruptedException {
        given()
                .when()
                .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
                .delete("/api/v2/block/ABC/hash/mockBlock")
                .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/block/ABC/hash/mockBlock"));
    }

    @Test
    @Order(10)
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