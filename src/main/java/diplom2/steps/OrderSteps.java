package diplom2.steps;

import io.restassured.response.ValidatableResponse;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    private static final String ORDERS = "/api/orders";

    public ValidatableResponse createOrderWithAuthorization(Map<String, List<String>> ingredients, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .body(ingredients)
                .post(ORDERS)
                .then();
    }

    public ValidatableResponse createOrderWithoutAuthorization(Map<String, List<String>> ingredients) {
        return given()
                .body(ingredients)
                .post(ORDERS)
                .then();
    }

    public ValidatableResponse getOrdersUserWithAuthorization(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS)
                .then();
    }

    public ValidatableResponse createOrderWithoutIngredients() {
        return given()
                .post(ORDERS)
                .then();
    }


}
