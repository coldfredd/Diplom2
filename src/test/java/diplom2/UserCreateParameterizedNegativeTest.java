package diplom2;

import diplom2.model.User;
import diplom2.steps.UserSteps;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class UserCreateParameterizedNegativeTest extends AbstractTest {
    private final String email;
    private final String password;
    private final String name;

    private final UserSteps userSteps = new UserSteps();
    private User user;

    public UserCreateParameterizedNegativeTest(String email,String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, "password", "name"},
                {"test@email.com", null,"name"},
                {"test@email.com", "password", null},
                {null, null, null},
        });
    }

    @Before
    public void setUp() {
        user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
    }
    @Test
    @DisplayName("Check status code 403 of /api/auth/register")
    @Description("Parameterized test for /api/auth/register endpoint. If none of the fields are requested, an error is returned")
    public void testCreatingUserWithoutRequiredFields() {
        ValidatableResponse response = createUser();
        checkStatusCodeAndMessage(response);
    }
    @Step("Create a user")
    public ValidatableResponse createUser() {
        return userSteps.createUser(user);
    }
    @Step("Check status code 403 and body contains error message")
    public void checkStatusCodeAndMessage(ValidatableResponse response) {
        response
                .statusCode(403)
                .body("message", Matchers.is("Email, password and name are required fields"));
    }
}

