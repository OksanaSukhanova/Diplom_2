package ru.yandex.praktikum.response;

import java.util.List;

public class ReceiveOrdersResponse {
    private boolean success;
    private List<OrderResponse> orders;
    private int total;
    private int totalToday;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<OrderResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponse> orders) {
        this.orders = orders;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalToday() {
        return totalToday;
    }

    public void setTotalToday(int totalToday) {
        this.totalToday = totalToday;
    }

    public OrderResponse getOrderById(String id) {
        OrderResponse orderResponse = new OrderResponse();
        for (OrderResponse orderResponse1: orders) {
            if (orderResponse1.get_id().equals(id)) {
                orderResponse = orderResponse1;
                break;
            }
        }
        return orderResponse;
    }
}
