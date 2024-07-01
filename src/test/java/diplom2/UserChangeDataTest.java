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

public class UserChangeDataTest extends AbstractTest{
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
    @DisplayName("Check status code 200 of /api/auth/user")
    @Description("The test verifies that a successful request returns new Name")
    public void changeUserNameTest(){
        String accessToken = userSteps.login(user).extract().body().path("accessToken");
        user.setName(faker.name().firstName());
        ValidatableResponse response = userSteps.changeUser(user, accessToken);
        verifyStatusCodeAndBodyName(response);
    }
    @Test
    @DisplayName("Check status code 200 of /api/auth/user")
    @Description("The test verifies that a successful request returns new Email")
    public void changeUserEmailTest(){
        String accessToken = userSteps.login(user).extract().body().path("accessToken");
        user.setEmail(faker.internet().emailAddress());
        ValidatableResponse response = userSteps.changeUser(user, accessToken);
        verifyStatusCodeAndBodyEmail(response);
    }
    @Test
    @DisplayName("Check status code 401 of /api/auth/user")
    @Description("Verify status code is 401 and body contains error message")
    public void changeUserNameWithoutAuthorizationTest(){
        user.setName(faker.name().firstName());
        ValidatableResponse response = userSteps.changeUser(user, " ");;
        checkStatusCodeAndMessageError(response);
    }
    @Test
    @DisplayName("Check status code 401 of /api/auth/user")
    @Description("Verify status code is 401 and body contains error message")
    public void changeUserEmailWithoutAuthorizationTest(){
        user.setEmail(faker.internet().emailAddress());
        ValidatableResponse response = userSteps.changeUser(user, " ");
        checkStatusCodeAndMessageError(response);
    }

    @Step("Verify status code is 200 and success is true")
    public void verifyStatusCodeAndBodyName(ValidatableResponse response) {
        response
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name",equalTo(user.getName()));
    }
    @Step("Verify status code is 200 and success is true")
    public void verifyStatusCodeAndBodyEmail(ValidatableResponse response) {
        response
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email",equalTo(user.getEmail()));
    }
    @Step("Verify status code is 401 and body contains error message")
    public void checkStatusCodeAndMessageError(ValidatableResponse response) {
        response
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
    @After
    public void deleteUser() {
        String accessToken = userSteps.login(user).extract().body().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
