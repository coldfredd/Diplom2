package diplom2.steps;

import diplom2.model.User;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserSteps {
    private static final String USER_REGISTER = "/api/auth/register";
    private static final String LOGIN = "api/auth/login";
    private static final String USER_DELETE = "api/auth/user";
    private static final String USER_CHANGE = "/api/auth/user";
    @Step("Create user")
    public ValidatableResponse createUser(User user){

        return given()
                .body(user)
                .when()
                .post(USER_REGISTER)
                .then();
    }
    @Step("Login user")
    public ValidatableResponse login(User user) {

        return  given()
                .body(user)
                .when()
                .post(LOGIN)
                .then();
    }
    @Step("Delete user")
    public static ValidatableResponse deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(USER_DELETE)
                .then();
    }
    @Step("Change user")
    public ValidatableResponse changeUser (User user, String accessToken){
        return given()
                .header("Authorization", accessToken)
                .body(user)
                .patch(USER_CHANGE)
                .then();
    }
}
