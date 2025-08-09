package com.aditi.menu.menu_backend.dto;

import lombok.Data;

@Data
public class OrderItemResponseDto {
    private Long id;
    private MenuItemResponseDto menuItem;
    private int quantity;
    private int unitPrice;
    private int lineTotal;
}
