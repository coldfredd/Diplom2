package diplom2.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    private static final String ORDERS = "/api/orders";
    @Step("Create order with authorization")
    public ValidatableResponse createOrderWithAuthorization(Map<String, List<String>> ingredients, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .body(ingredients)
                .post(ORDERS)
                .then();
    }
    @Step("Create order without Authorization")
    public ValidatableResponse createOrderWithoutAuthorization(Map<String, List<String>> ingredients) {
        return given()
                .body(ingredients)
                .post(ORDERS)
                .then();
    }
    @Step("get order with access token")
    public ValidatableResponse getOrdersUserWithAuthorization(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS)
                .then();
    }
    @Step("Create Order without ingredients")
    public ValidatableResponse createOrderWithoutIngredients() {
        return given()
                .post(ORDERS)
                .then();
    }


}
