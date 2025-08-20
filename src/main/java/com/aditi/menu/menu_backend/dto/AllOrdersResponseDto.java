package com.aditi.menu.menu_backend.dto;

import java.util.List;

public class AllOrdersResponseDto {
    private List<OrderResponseDto> orders;
    private OrderStatusSummaryDto summary;

    public AllOrdersResponseDto() {
    }

    public AllOrdersResponseDto(List<OrderResponseDto> orders, OrderStatusSummaryDto summary) {
        this.orders = orders;
        this.summary = summary;
    }

    // Getters and Setters
    public List<OrderResponseDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponseDto> orders) {
        this.orders = orders;
    }

    public OrderStatusSummaryDto getSummary() {
        return summary;
    }

    public void setSummary(OrderStatusSummaryDto summary) {
        this.summary = summary;
    }
}