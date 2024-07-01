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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;

public class UserLoginTest extends AbstractTest{
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
        userSteps.createUser(user);
    }
    @Test
    @DisplayName("Check status code 200 of /api/auth/login")
    @Description("The test verifies that a successful request returns body success: true")
    public void loginUserTest(){
        ValidatableResponse response = userSteps.login(user);
        verifyStatusCodeAndBodySuccess(response);
    }
    @Test
    @DisplayName("Check status code 401 of /api/auth/login")
    @Description("Check can't login user if enter password incorrectly")
    public void incorrectPasswordTest(){
        user.setPassword(faker.internet().password());
        ValidatableResponse response = userSteps.login(user);
        checkStatusCodeAndMessageError(response);
    }
    @Test
    @DisplayName("Check status code 401 of /api/auth/login")
    @Description("Check can't login user if enter email incorrectly")
    public void incorrectEmailTest(){
        user.setEmail(faker.internet().emailAddress());
        ValidatableResponse response = userSteps.login(user);
        checkStatusCodeAndMessageError(response);
    }
    @Step("Verify status code is 200 and success is true")
    public void verifyStatusCodeAndBodySuccess(ValidatableResponse response) {
        response
                .statusCode(200)
                .body("success", equalTo(true));
    }
    @Step("Verify status code is 401 and body contains error message")
    public void checkStatusCodeAndMessageError(ValidatableResponse response) {
        response
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
    @After
    public void deleteUser() {
        String accessToken = userSteps.login(user).extract().body().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
