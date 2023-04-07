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

public class UserUpdateTest {
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

    @DisplayName("Изменение данных авторизованного пользователя")
    @Description("Проверка, что авторизованный пользователь может изменить любые свои данные")
    @Test
    public void updateUserWithAuthorizationIsPossible() {
        // Генерируем новые данные пользователя
        User newUser = UserGenerator.getRandom();

        // Обновляем пользователя
        ValidatableResponse updateResponse = userClient.update(newUser, bearerToken);

        // Проверяем, что обновление успешно и в ответе новые данные
        updateResponse.statusCode(200).assertThat()
                .body("success", is(true))
                .body("user.email", equalTo(newUser.getEmail()))
                .body("user.name", equalTo(newUser.getName()));
    }

    @DisplayName("Изменение данных неавторизованного пользователя")
    @Description("Проверка, что неавторизованный пользователь не может изменить свои данные")
    @Test
    public void updateUserWithoutAuthorizationIsImpossible() {
        // Генерируем новые данные пользователя
        User newUser = UserGenerator.getRandom();

        // Обновляем пользователя
        ValidatableResponse updateResponse = userClient.update(newUser, "");

        // Проверяем, что обновление не удалось и получена корректная ошибка
        updateResponse.statusCode(401).assertThat()
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @After
    public void clearData() {
        userClient.delete(user.getEmail(), bearerToken);
    }
}
