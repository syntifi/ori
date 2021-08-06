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
public class TestBlockResouces {

    private static Object LOCK = new Object();

    @Test
    @Order(1)
    public void testPostBlock() throws Exception {
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("parent", "parent");
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        block.put("validator", "validator");
        given()
          .body(block.toString())
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
          .when()
          .post("/block")
          .then()
             .statusCode(200)
             .body("created", equalTo("/block/mockBlock"));
        synchronized (LOCK) {
            LOCK.wait(5000);
        }
    }

    @Test
    @Order(2)
    public void testGetBlock() {
        given()
          .when()
          .get("/block/mockBlock")
          .then()
             .statusCode(200)
             .body("era", equalTo(0))
             .body("hash", equalTo("mockBlock"))
             .body("height", equalTo(0))
             .body("parent", equalTo("parent"))
             .body("root", equalTo("root"))
             .body("timeStamp", equalTo("2099-08-05T00:00:00.000+0000"))
             .body("validator", equalTo("validator"));
    }

    @Test
    @Order(3)
    public void testGetBlocks() {
        given()
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .when()
            .get("/block")
            .then()
                .statusCode(200)
                .body("[0].era", equalTo(0))
                .body("[0].hash", equalTo("mockBlock"))
                .body("[0].height", equalTo(0))
                .body("[0].parent", equalTo("parent"))
                .body("[0].root", equalTo("root"))
                .body("[0].timeStamp", equalTo("2099-08-05T00:00:00.000+0000"))
                .body("[0].validator", equalTo("validator"));
    }

    @Test
    @Order(4)
    public void testGetNonExistingBlock() {
        given()
          .when()
          .get("/block/testBlock")
          .then()
             .statusCode(404)
             .body("error", equalTo("Not Found"));
    }

    @Test
    @Order(5)
    public void testPostBlockWrongDateType() {
        var block = new JsonObject();
        block.put("era", 0);
        block.put("hash", "mockBlock");
        block.put("height", 0);
        block.put("parent", "parent");
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05");
        block.put("validator", "validator");
        given()
          .body(block.toString())
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
          .when()
          .post("/block")
          .then()
             .statusCode(400)
             .body("error", containsString("Date"));
    }

    @Test
    @Order(6)
    public void testPostBlockWithoutHash() {
        var block = new JsonObject();
        block.put("era", 0);
        block.put("height", 0);
        block.put("parent", "parent");
        block.put("root", "root");
        block.put("timeStamp", "2099-08-05T00:00:00.000+0000");
        block.put("validator", "validator");
        given()
          .body(block.toString())
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
          .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
          .when()
          .post("/block")
          .then()
             .statusCode(404)
             .body("error", equalTo("Block hash missing"));
    }

    @Test
    @Order(7)
    public void testDeleteBlock() throws InterruptedException {
        given()
            .when()
            .header(HttpHeaders.ACCEPT, MediaType.MEDIA_TYPE_WILDCARD)
            .delete("/block/mockBlock")
            .then()
                .statusCode(200)
                .body("method", equalTo("DELETE"))
                .body("uri", equalTo("/block/mockBlock"));
    }
}