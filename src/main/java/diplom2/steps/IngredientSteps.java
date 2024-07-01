package diplom2.steps;

import io.qameta.allure.Step;

import java.util.List;

import static io.restassured.RestAssured.given;

public class IngredientSteps {
    private static final String INGREDIENTS = "api/ingredients";
    @Step("Get all Ingredients")
    public List<String> getAllIngredients() {
        return given()
                .get(INGREDIENTS)
                .then()
                .extract().path("data._id");
    }
}
