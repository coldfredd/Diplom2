package diplom2;

import com.github.javafaker.Faker;
import diplom2.model.User;
import diplom2.steps.IngredientSteps;
import diplom2.steps.OrderSteps;
import diplom2.steps.UserSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.hamcrest.core.IsEqual.equalTo;

public class OrderCreateTest extends AbstractTest{

    private final OrderSteps orderSteps = new OrderSteps();
    private final UserSteps userSteps = new UserSteps();

    private final IngredientSteps ingredientSteps = new IngredientSteps();
    private String accessToken;
    User user;
    Faker faker;

    @Before
    public void setUp() {
        faker = new Faker();
        user = new User();
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        user.setName(faker.name().firstName());
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Check status code 200 of /api/orders")
    @Description("Create order with Login and Ingredients")
    public void createOrderWithLoginTest(){
        accessToken = userSteps.createUser(user).extract().path("accessToken");
        List<String> ids = ingredientSteps.getAllIngredients();
        Collections.shuffle(ids);
        Map<String,List<String>> ingredients = Map.of("ingredients",List.of( ids.get(0),ids.get(1)));
        ValidatableResponse response = orderSteps.createOrderWithAuthorization(ingredients,accessToken);
        checkOrderCreate(response);
    }
    @Test
    @DisplayName("Check status code 200 of /api/orders")
    @Description("Create order with Ingredients and without Login")
    public void createOrderWithoutLoginTest(){
        List<String> ids = ingredientSteps.getAllIngredients();
        Collections.shuffle(ids);
        Map<String,List<String>> ingredients = Map.of("ingredients",List.of( ids.get(0),ids.get(1)));
        ValidatableResponse response = orderSteps.createOrderWithoutAuthorization(ingredients);
        checkOrderCreate(response);
    }
    @Test
    @DisplayName("Check status code 400 of /api/orders")
    @Description("Create order without Ingredients and Login")
    public void createOrderWithoutIngredientsLoginTest(){
        ValidatableResponse response = orderSteps.createOrderWithoutIngredients();
        checkOrderFailWithoutIngredients(response);
    }
    @Test
    @DisplayName("Check status code 500 of /api/orders")
    @Description("Create order when hash incorrect")
    public void createOrderInvalidHashIngredientTest(){
        List<String> ids = ingredientSteps.getAllIngredients();
        Collections.shuffle(ids);
        Map<String,List<String>> ingredients = Map.of("ingredients",List.of( (ids.get(0)+"123"),(ids.get(1))+"123"));
        ValidatableResponse response = orderSteps.createOrderWithoutAuthorization(ingredients);
        checkErrorStatus(response);
    }
    @Step("Check status code 200 and body success true")
    public void checkOrderCreate(ValidatableResponse response) {
        response
                .statusCode(200)
                .body("success", equalTo(true));
    }
    @Step("Check status code 400 and message error")
    public void checkOrderFailWithoutIngredients(ValidatableResponse response) {
        response
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }
    @Step("Check status code 500 and message error")
    public void checkErrorStatus(ValidatableResponse response) {
        response
                .statusCode(500);
    }
    @After
    public void deleteUser() {
        String accessToken = userSteps.login(user).extract().body().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}