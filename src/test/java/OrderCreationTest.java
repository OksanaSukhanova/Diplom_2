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
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.OrderGenerator;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserGenerator;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;

public class OrderCreationTest {
    private Order order;
    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String bearerToken;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new AllureRestAssured());
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        order = OrderGenerator.getRandom();
        user = UserGenerator.getRandom();
    }

    @DisplayName("Создание заказа от авторизованного пользователя")
    @Description("Проверка, что авторизованный пользователь может создать заказ")
    @Test
    public void orderCreationWithAuthorizationIsPossible() {
        // Создаем пользователя и получаем его токен
        bearerToken = userClient.create(user).extract().path("accessToken");

        // Создаем заказ от авторизованного пользователя
        ValidatableResponse createResponse = orderClient.create(order, bearerToken);

        // Проверяем, что заказ создан и в ответе содержатся данные авторизованного пользователя
        createResponse.statusCode(200).assertThat()
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("order.owner.name", equalTo(user.getName()))
                .body("order.owner.email", equalTo(user.getEmail()));
    }

    @DisplayName("Создание заказа от неавторизованного пользователя")
    @Description("Проверка, что неавторизованный пользователь может создать заказ")
    @Test
    public void orderCreationWithoutAuthorizationIsPossible() {
        bearerToken = "";

        // Создаем заказ с пустым токеном
        ValidatableResponse createResponse = orderClient.create(order, bearerToken);

        // Проверяем, что заказ создан
        createResponse.statusCode(200).assertThat()
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка, что заказ без ингредиентов не может быть создан")
    @Test
    public void orderCreationWithoutIngredientsIsImpossible() {
        bearerToken = "";
        // Убираем ингредиенты из заказа
        order.setIngredients(new ArrayList<>());

        // Создаем заказ без ингредиентов
        ValidatableResponse createResponse = orderClient.create(order, bearerToken);

        // Проверяем, что заказ не создан и в ответе возвращается корректная ошибка
        createResponse.statusCode(400).assertThat()
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @DisplayName("Создание заказа с невалидными ингредиентами")
    @Description("Проверка, что заказ с невалидными ингредиентами не может быть создан")
    @Test
    public void orderCreationWithInvalidIngredientsIsImpossible() {
        bearerToken = "";
        // Заменяем ингредиенты в заказе на невалидный хэш
        ArrayList<String> invalidIngredient = new ArrayList<>();
        invalidIngredient.add("someIngredient");
        order.setIngredients(invalidIngredient);

        // Создаем заказ с невалидными ингредиентами
        ValidatableResponse createResponse = orderClient.create(order, bearerToken);

        // Проверяем, что заказ не создан
        createResponse.statusCode(500);
    }

    @After
    public void clearData() {
        userClient.delete(user.getEmail(), bearerToken);
    }
}
