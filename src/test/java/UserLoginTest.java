import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;
import ru.yandex.praktikum.model.UserGenerator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UserLoginTest {
    private UserClient userClient;
    private User user;
    private String bearerToken;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandom();
        bearerToken = userClient.create(user).extract().path("accessToken");
    }

    @DisplayName("Авторизация под существующим пользователем")
    @Description("Проверка, что созданный пользователь может авторизоваться")
    @Test
    public void authorizationFromExistingUserIsSuccessful() {
        // Авторизуемся под существующим пользователем
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user));

        // Проверяем, что авторизация успешна и email/имя в ответе соответствуют созданному пользователю
        loginResponse.statusCode(200).assertThat().body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        // Получаем токен для удаления пользователя
        bearerToken = loginResponse.extract().path("accessToken");
    }

    @DisplayName("Авторизация с некорректным email")
    @Description("Проверка, что пользователь с некорректным email не может авторизоваться")
    @Test
    public void authorizationWithIncorrectEmailIsNotSuccessful() {
        // Меняем email на случайный и авторизуемся
        user.setEmail(RandomStringUtils.random(10));
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user));

        // Проверяем, что авторизация не удалась и получена корректная ошибка
        loginResponse.statusCode(401).assertThat()
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @DisplayName("Авторизация без email")
    @Description("Проверка, что пользователь без email не может авторизоваться")
    @Test
    public void authorizationWithoutEmailIsNotSuccessful() {
        // Удаляем email и авторизуемся
        user.setEmail(null);
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user));

        // Проверяем, что авторизация не удалась и получена корректная ошибка
        loginResponse.statusCode(401).assertThat()
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @DisplayName("Авторизация с некорректным паролем")
    @Description("Проверка, что пользователь с некорректным паролем не может авторизоваться")
    @Test
    public void authorizationWithIncorrectPasswordIsNotSuccessful() {
        // Меняем пароль на случайный и авторизуемся
        user.setPassword(RandomStringUtils.random(10));
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user));

        // Проверяем, что авторизация не удалась и получена корректная ошибка
        loginResponse.statusCode(401).assertThat()
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @DisplayName("Авторизация без пароля")
    @Description("Проверка, что пользователь без пароля не может авторизоваться")
    @Test
    public void authorizationWithoutPasswordIsNotSuccessful() {
        // Удаляем пароль и авторизуемся
        user.setPassword(null);
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user));

        // Проверяем, что авторизация не удалась и получена корректная ошибка
        loginResponse.statusCode(401).assertThat()
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @After
    public void clearData() {
        userClient.delete(user.getEmail(), bearerToken);
    }
}
