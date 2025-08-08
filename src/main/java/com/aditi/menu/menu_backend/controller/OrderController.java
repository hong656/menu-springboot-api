package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.OrderRequestDto;
import com.aditi.menu.menu_backend.dto.OrderStatusUpdateRequestDto;
import com.aditi.menu.menu_backend.entity.Order;
import com.aditi.menu.menu_backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        Order createdOrder = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequestDto statusUpdateDto) {
        Order updatedOrder = orderService.updateOrderStatus(id, statusUpdateDto.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }
}