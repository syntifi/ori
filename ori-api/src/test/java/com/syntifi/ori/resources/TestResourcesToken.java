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
public class TestResourcesToken {

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
    public void testGetToken() {
        given()
                .when()
                .get("/api/v2/token/ABC")
                .then()
                .statusCode(200)
                .body("symbol", equalTo("ABC"))
                .body("name", equalTo("Token ABC"))
                .body("protocol", equalTo("A"));
    }

    @Test
    @Order(3)
    public void testGetTokens() {
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .get("/api/v2/token")
                .then()
                .statusCode(200)
                .body("[0].symbol", equalTo("ABC"))
                .body("[0].name", equalTo("Token ABC"))
                .body("[0].protocol", equalTo("A"));
    }

    @Test
    @Order(4)
    public void testGetNonExistingToken() {
        given()
                .when()
                .get("/api/v2/token/EFG")
                .then()
                .statusCode(404)
                .body("error", equalTo("EFG not found"));
    }

    @Test
    @Order(5)
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