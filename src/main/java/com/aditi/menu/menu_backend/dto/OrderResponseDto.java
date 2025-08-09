package com.aditi.menu.menu_backend.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private RestaurantTableResponseDto table;
    private Integer status;
    private String remark;
    private Integer totalCents;
    private Instant placedAt;
    private Instant updatedAt;
    private List<OrderItemResponseDto> orderItems;
}
