package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.BurgerRestClient;
import ru.yandex.praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BurgerRestClient {
    private static final String ORDER_URI = BASE_URI + "/orders";

    public ValidatableResponse get() {
        return given()
                .spec(getBaseReqSpec())
                .when().get(BASE_URI + "/ingredients").then();
    }

    @Step("Create order {order}")
    public ValidatableResponse create(Order order, String bearerToken) {
        return given()
                .headers("Authorization", bearerToken)
                .spec(getBaseReqSpec())
                .body(order)
                .when().post(ORDER_URI).then();
    }

    @Step("Receive user's orders")
    public ValidatableResponse receive(String bearerToken) {
        return given()
                .headers("Authorization", bearerToken)
                .spec(getBaseReqSpec())
                .when().get(ORDER_URI).then();
    }
}
