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

public class OrderGetTest extends AbstractTest {
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
    @DisplayName("Check status code 200 of GET /api/orders")
    @Description("Get order with Login, check status 200")
    public void getOrderWithLogin(){
        accessToken = createUser().extract().path("accessToken");
        List<String> ids = getIngredients();
        Collections.shuffle(ids);
        Map<String,List<String>> ingredients = Map.of("ingredients",List.of( ids.get(0),ids.get(1)));
        createOrderWithAuthorization(ingredients);
        ValidatableResponse response = getOrdersUserWithAuthorization();
        checkUserOrder(response);
    }

    @Test
    @DisplayName("Check status code 401 of /api/orders")
    @Description("Create order without Login, check status 401")
    public void getOrderWithoutLogin(){
        ValidatableResponse response = getOrderWithoutAuthorization();
        checkErrorMessage(response);
    }
    @Step("get order without access token")
    private ValidatableResponse getOrderWithoutAuthorization() {
        return orderSteps.getOrdersUserWithAuthorization(" ");
    }
    @Step("Check status code 401 and message error")
    public void checkErrorMessage(ValidatableResponse response) {
        response
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
    @Step("Check status code 200 and body success true")
    public void checkUserOrder(ValidatableResponse response) {
        response
                .statusCode(200)
                .body("success", equalTo(true));
    }
    @Step("get order with access token")
    private ValidatableResponse getOrdersUserWithAuthorization() {
        return orderSteps.getOrdersUserWithAuthorization(accessToken);
    }
    @Step("create order with authorization")
    private void createOrderWithAuthorization(Map<String, List<String>> ingredients) {
        orderSteps.createOrderWithAuthorization(ingredients,accessToken);
    }
    @Step("Create a user")
    public ValidatableResponse createUser() {
        return userSteps.createUser(user);
    }
    @Step("Get all ingredients")
    private List<String> getIngredients() {
        return ingredientSteps.getAllIngredients();
    }
    @After
    public void deleteUser() {
        String accessToken = userSteps.login(user).extract().body().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
