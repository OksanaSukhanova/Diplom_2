package ru.yandex.praktikum.model;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class UserGenerator {
    public static User getRandom() {
        String email = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ROOT) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphabetic(10).toLowerCase();
        String name = RandomStringUtils.randomAlphabetic(10).toLowerCase(Locale.ROOT);
        return new User(email, password, name);
    }
}
