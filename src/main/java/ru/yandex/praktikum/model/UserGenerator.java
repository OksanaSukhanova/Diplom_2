package ru.yandex.praktikum.model;

import org.apache.commons.lang3.RandomStringUtils;

public class UserGenerator {
    public static User getRandom() {
        String email = getRandomEmail();
        String password = getRandomPassword();
        String name = getRandomName();
        return new User(email, password, name);
    }

    public static String getRandomEmail() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
    }

    public static String getRandomPassword() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }

    public static String getRandomName() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }
}
