package com.aditi.menu.menu_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantTableResponseDto {
    private Long id;
    private Integer number;
    private List<OrderInTableResponseDto> orders;
}