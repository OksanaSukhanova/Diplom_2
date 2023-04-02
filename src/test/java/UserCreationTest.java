import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserGenerator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UserCreationTest {
    private UserClient userClient;
    private User user;
    private String bearerToken = "";

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandom();
    }

    @DisplayName("Создание пользователя с уникальными данными")
    @Description("Проверка, что пользователь с уникальными данными может быть создан")
    @Test
    public void userCreationWithUniqueDataIsPossible() {
        // Создаем пользователя
        ValidatableResponse createResponse = userClient.create(user);

        // Проверяем, что пользователь создан и email/имя в ответе соответствуют заданным
        createResponse.statusCode(200).assertThat()
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        // Получаем токен для удаления пользователя
        bearerToken = createResponse.extract().path("accessToken");
    }

    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка, что пользователь с повторяющимися данными не может быть создан")
    @Test
    public void duplicateUserCreationIsImpossible() {
        // Создаем пользователя и извлекаем токен для удаления
        bearerToken = userClient.create(user).extract().path("accessToken");

        // Повторно создаем пользователя с теми же данными
        ValidatableResponse createResponse = userClient.create(user);

        // Проверяем, что пользователь не создан и получена корректная ошибка
        createResponse.statusCode(403).assertThat()
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @DisplayName("Создание пользователя без email")
    @Description("Проверка, что пользователь без email не может быть создан")
    @Test
    public void userWithoutEmailCreationIsImpossible() {
        // Удаляем email и создаем пользователя
        user.setEmail(null);
        ValidatableResponse createUser = userClient.create(user);

        // Проверяем, что пользователь не создан и получена корректная ошибка
        createUser.statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @DisplayName("Создание пользователя без имени")
    @Description("Проверка, что пользователь без имени не может быть создан")
    @Test
    public void userWithoutNameCreationIsImpossible() {
        // Удаляем имя и создаем пользователя
        user.setName(null);
        ValidatableResponse createUser = userClient.create(user);

        // Проверяем, что пользователь не создан и получена корректная ошибка
        createUser.statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка, что пользователь без пароля не может быть создан")
    @Test
    public void userWithoutPasswordCreationIsImpossible() {
        // Удаляем пароль и создаем пользователя
        user.setPassword(null);
        ValidatableResponse createUser = userClient.create(user);

        // Проверяем, что пользователь не создан и получена корректная ошибка
        createUser.statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @After
    public void clearData() {
        userClient.delete(user.getEmail(), bearerToken);
    }
}
