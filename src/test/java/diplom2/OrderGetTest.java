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
        accessToken = userSteps.createUser(user).extract().path("accessToken");
        List<String> ids = ingredientSteps.getAllIngredients();
        Collections.shuffle(ids);
        Map<String,List<String>> ingredients = Map.of("ingredients",List.of( ids.get(0),ids.get(1)));
        orderSteps.createOrderWithAuthorization(ingredients,accessToken);
        ValidatableResponse response = orderSteps.getOrdersUserWithAuthorization(accessToken);
        checkUserOrder(response);
    }

    @Test
    @DisplayName("Check status code 401 of /api/orders")
    @Description("Create order without Login, check status 401")
    public void getOrderWithoutLogin(){
        ValidatableResponse response = orderSteps.getOrdersUserWithAuthorization(" ");
        checkErrorMessage(response);
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
    @After
    public void deleteUser() {
        String accessToken = userSteps.login(user).extract().body().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
