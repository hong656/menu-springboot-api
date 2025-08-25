package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.OrderRequestDto;
import com.aditi.menu.menu_backend.dto.OrderResponseDto;
import com.aditi.menu.menu_backend.dto.OrderStatusSummaryDto;
import com.aditi.menu.menu_backend.dto.OrderStatusUpdateRequestDto;
import com.aditi.menu.menu_backend.service.OrderService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto createdOrder = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponseDto> orderPage = orderService.getAllOrders(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("items", orderPage.getContent());
        response.put("currentPage", orderPage.getNumber());
        response.put("pageSize", orderPage.getSize());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("isFirst", orderPage.isFirst());
        response.put("isLast", orderPage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<OrderStatusSummaryDto> getOrderSummary() {
        return ResponseEntity.ok(orderService.getOrderStatusSummary());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequestDto statusUpdateDto) {
        OrderResponseDto updatedOrder = orderService.updateOrderStatus(id, statusUpdateDto.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }
}
