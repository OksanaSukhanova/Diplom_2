import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserGenerator;

import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class UserCreationParameterizedTest {
    private final String email;
    private final String password;
    private final String name;
    private User user;
    private UserClient userClient;

    public UserCreationParameterizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Object[][] getUserData() {
        return new Object[][]{
                {null, UserGenerator.getRandomPassword(), UserGenerator.getRandomName()},
                {UserGenerator.getRandomEmail(), null, UserGenerator.getRandomName()},
                {UserGenerator.getRandomPassword(), UserGenerator.getRandomPassword(), null}
        };
    }

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = new User(email, password, name);
    }

    @DisplayName("Создание пользователя без обязательного поля")
    @Description("Проверка, что пользователь с одним незаполненным обязательным полем не может быть создан")
    @Test
    public void userWithoutRequiredCredentialCreationIsImpossible() {
        // Создаем пользователя с незаполненным полем
        ValidatableResponse createUser = userClient.create(user);

        // Проверяем, что пользователь не создан и получена корректная ошибка
        createUser.statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }
}
