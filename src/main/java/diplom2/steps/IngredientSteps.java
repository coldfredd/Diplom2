package diplom2.steps;

import java.util.List;

import static io.restassured.RestAssured.given;

public class IngredientSteps {
    private static final String INGREDIENTS = "api/ingredients";
    public List<String> getAllIngredients() {
        return given()
                .get(INGREDIENTS)
                .then()
                .extract().path("data._id");
    }
}
