package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.OrderRequestDto;
import com.aditi.menu.menu_backend.dto.OrderResponseDto;
import com.aditi.menu.menu_backend.dto.OrderStatusSummaryDto;
import com.aditi.menu.menu_backend.dto.OrderStatusUpdateRequestDto;
import com.aditi.menu.menu_backend.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('order:create')")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto createdOrder = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('order:read')")
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponseDto> orderPage = orderService.getAllOrders(pageable, search, status);

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

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('order:read')")
    public ResponseEntity<Map<String, Object>> getAllOrdersWithoutPagination() {
        List<OrderResponseDto> orders = orderService.getAllOrdersWithoutPagination();
        OrderStatusSummaryDto summary = orderService.getOrderStatusSummary();

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("summary", summary);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('order:read')")
    public ResponseEntity<OrderStatusSummaryDto> getOrderSummary() {
        return ResponseEntity.ok(orderService.getOrderStatusSummary());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('order:read')")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('order:delete')")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequestDto statusUpdateDto) {
        OrderResponseDto updatedOrder = orderService.updateOrderStatus(id, statusUpdateDto.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }
}
