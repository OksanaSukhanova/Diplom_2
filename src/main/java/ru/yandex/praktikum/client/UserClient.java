package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.BurgerRestClient;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends BurgerRestClient {
    private static final String USER_URI = BASE_URI + "/auth";

    @Step("Create user {user}")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when().post(USER_URI + "/register").then();
    }

    @Step("Login as user {userCredentials}")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseReqSpec())
                .body(userCredentials)
                .when().post(USER_URI + "/login").then();
    }

    @Step("Update user with new data {user}")
    public ValidatableResponse update(User user, String bearerToken) {
        return given()
                .spec(getBaseReqSpec())
                .headers("Authorization", bearerToken)
                .body(user)
                .when().patch(USER_URI + "/user").then();
    }

    @Step("Delete user by email {email}")
    public void delete(String email, String bearerToken) {
        String json = String.format("{\"email\": \"%s\"}", email);

        given()
                .spec(getBaseReqSpec())
                .headers("Authorization", bearerToken)
                .body(json)
                .when().delete(USER_URI + "/user").then();
    }
}
