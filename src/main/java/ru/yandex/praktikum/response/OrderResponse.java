package ru.yandex.praktikum.response;

import java.util.ArrayList;

public class OrderResponse {
    private ArrayList<String> ingredients;
    private String _id;
    private String status;
    private int number;
    private String createdAt;
    private String updatedAt;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public java.lang.String get_id() {
        return _id;
    }

    public void set_id(java.lang.String _id) {
        this._id = _id;
    }

    public java.lang.String getStatus() {
        return status;
    }

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public java.lang.String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.lang.String createdAt) {
        this.createdAt = createdAt;
    }

    public java.lang.String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.lang.String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
