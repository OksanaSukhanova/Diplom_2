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
import ru.yandex.praktikum.response.OrderResponse;
import ru.yandex.praktikum.response.ReceiveOrdersResponse;

import static org.junit.Assert.*;

public class OrderReceivingTest {
    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private Order order;
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

    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка, что авторизованный пользователь может получить список своих заказов")
    @Test
    public void receivingOrdersFromAuthorizedUserIsPossible() {
        // Получаем токен для получения заказа и последующего удаления пользователя
        bearerToken = userClient.create(user).extract().path("accessToken");
        // Получаем id заказа для проверки полей конкретного заказа
        /* Коммент для ревьюера: в этом тесте предполагается, что заказ один.
        Но считаю такой подход более универсальным и правильным, чем брать первый заказ из списка */
        String id = orderClient.create(order, bearerToken).extract().path("order._id");

        // Запрашиваем заказы пользователя
        ValidatableResponse receiveResponse = orderClient.receive(bearerToken);

        // Проверяем, что запрос выполнен
        int statusCode = receiveResponse.extract().statusCode();
        assertEquals("Статус ответа некорректный", 200, statusCode);

        // Десериализуем ответ
        ReceiveOrdersResponse receiveOrdersResponse = receiveResponse.extract().body().as(ReceiveOrdersResponse.class);
        // Получаем нужный заказ по id
        OrderResponse orderResponse = receiveOrdersResponse.getOrderById(id);

        assertEquals("Некорректные ингредиенты в заказе", order.getIngredients(), orderResponse.getIngredients());
        assertTrue("Номер заказа не получен", orderResponse.getNumber() != 0);
        assertNotNull("Название бургера не получено", orderResponse.getName());
        assertEquals("Получено некорректное количество заказов", 1, receiveOrdersResponse.getOrders().size());
        // На этой проверке тест падает, так как система считает общее количество всех заказов, а не конкретного пользователя
        assertEquals("Общее количество заказов пользователя подсчитано неверно", 1, receiveOrdersResponse.getTotal());
    }

    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Проверка, что неавторизованный пользователь не может получить список своих заказов")
    @Test
    public void receivingOrdersFromUnauthorizedUserIsImpossible() {
        // Задаем пустой токен
        bearerToken = "";
        // Создаем заказ от неавторизованного пользователя
        orderClient.create(order, bearerToken);

        // Запрашиваем заказы неавторизованного пользователя
        ValidatableResponse receiveResponse = orderClient.receive(bearerToken);

        // Проверяем, что запрос не выполнен
        int statusCode = receiveResponse.extract().statusCode();
        assertEquals("Статус ответа некорректный", 401, statusCode);

        // Проверяем ответ
        boolean success = receiveResponse.extract().path("success");
        assertFalse("Некорректный ответ", success);

        // Проверяем, что получено корректное сообщение
        String message = receiveResponse.extract().path("message");
        assertEquals("Получено некорректное сообщение", "You should be authorised", message);
    }

    @After
    public void clearData() {
        userClient.delete(user.getEmail(), bearerToken);
    }
}
