package diplom2;

import com.github.javafaker.Faker;
import diplom2.model.User;
import diplom2.steps.UserSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class UserCreateTest extends AbstractTest{

    private final UserSteps userSteps = new UserSteps();
    User user;
    Faker faker;

    @Before
    public void setUp(){
        faker = new Faker();
        user = new User();
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(faker.internet().password());
        user.setName(faker.name().firstName());
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    }
    @Test
    @DisplayName("Check status code 200 of /api/auth/register")
    @Description("Check create unique user test for /api/auth/register endpoint. And body have success: true. Email and name match, accessToken and refreshToken not null")
    public void createUniqueUser() {
        ValidatableResponse response = userSteps.createUser(user);
        checkUserHaveOk(response);
    }
    @Test
    @Description("Create a user who is already exist")
    public void createUserAlreadyRegistered() {
        userSteps.createUser(user);
        ValidatableResponse response = userSteps.createUser(user);
        checkUserAlreadyExists(response);
    }
    @Step("Check status code 200 and body have Ok")
    public void checkUserHaveOk(ValidatableResponse response) {
        response
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }
    @Step("Check status code 403 and body contains error message")
    public void checkUserAlreadyExists(ValidatableResponse response) {
        response
                .statusCode(403)
                .body("message", Matchers.is("User already exists"));
    }
    @After
    public void deleteUser() {
        String accessToken = userSteps.login(user).extract().body().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
