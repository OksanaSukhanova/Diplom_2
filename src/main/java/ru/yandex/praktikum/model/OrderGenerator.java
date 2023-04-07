package ru.yandex.praktikum.model;

import ru.yandex.praktikum.client.OrderClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderGenerator {
    public static Order getRandom() {
        OrderClient orderClient = new OrderClient();
        List<String> orderIds = orderClient.get().extract().path("data._id");

        ArrayList<String> ingredients = new ArrayList<>();
        Random indexes = new Random();
        ingredients.add(orderIds.get(indexes.nextInt(orderIds.size())));
        ingredients.add(orderIds.get(indexes.nextInt(orderIds.size())));
        return new Order(ingredients);
    }
}
