package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.dto.*;
import com.aditi.menu.menu_backend.entity.MenuItem;
import com.aditi.menu.menu_backend.entity.Order;
import com.aditi.menu.menu_backend.entity.OrderItem;
import com.aditi.menu.menu_backend.entity.RestaurantTable;
import com.aditi.menu.menu_backend.repository.MenuItemRepository;
import com.aditi.menu.menu_backend.repository.OrderRepository;
import com.aditi.menu.menu_backend.repository.RestaurantTableRepository;
import com.aditi.menu.menu_backend.specs.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final OrderSpecification orderSpecification;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository, RestaurantTableRepository restaurantTableRepository, OrderSpecification orderSpecification) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.orderSpecification = orderSpecification;
    }

    public Page<OrderResponseDto> getAllOrders(Pageable pageable, String search, Integer status) {
        Specification<Order> spec = orderSpecification.getOrders(search, status);
        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        List<OrderResponseDto> orderResponseDtos = orderPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(orderResponseDtos, pageable, orderPage.getTotalElements());
    }

    public List<OrderResponseDto> getAllOrdersWithoutPagination() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public OrderStatusSummaryDto getOrderStatusSummary() {
        List<Order> allOrders = orderRepository.findAll();
        long preparingCount = allOrders.stream().filter(order -> order.getStatus() == 1).count();
        long receivedCount = allOrders.stream().filter(order -> order.getStatus() == 2).count();
        long completedCount = allOrders.stream().filter(order -> order.getStatus() == 3).count();
        long canceledCount = allOrders.stream().filter(order -> order.getStatus() == 4).count();
        return new OrderStatusSummaryDto(preparingCount, receivedCount, completedCount, canceledCount);
    }

    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return convertToDto(order);
    }

    public OrderResponseDto updateOrderStatus(Long orderId, Integer newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        if (newStatus < 1 || newStatus > 4) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }
        order.setStatus(newStatus);
        return convertToDto(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        String token = orderRequestDto.getQrToken();
        RestaurantTable table = restaurantTableRepository.findByQrToken(token)
                .orElseThrow(() -> new RuntimeException("Table not found with QR Token: " + token));

        if (table.getStatus() != 1) {
            throw new RuntimeException("Table " + table.getNumber() + " is currently inactive.");
        }

        Order order = new Order();
        order.setTable(table);
        order.setRemark(orderRequestDto.getRemark());
        order.setStatus(1);

        List<OrderItem> orderItems = new ArrayList<>();
        int totalCents = 0;

        for (OrderItemRequestDto itemDto : orderRequestDto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + itemDto.getMenuItemId()));

            // Check if the menu item is active (status 1)
            if (menuItem.getStatus() != 1) {
                throw new RuntimeException("MenuItem '" + menuItem.getName() + "' is not available for order.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(menuItem.getPriceCents());
            orderItem.setLineTotal(menuItem.getPriceCents() * itemDto.getQuantity());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalCents += orderItem.getLineTotal();
        }

        order.setOrderItems(orderItems);
        order.setTotalCents(totalCents);

        return convertToDto(orderRepository.save(order));
    }
        
    private OrderResponseDto convertToDto(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(order.getId());

        RestaurantTableResponseDto tableDto = new RestaurantTableResponseDto();
        tableDto.setId(order.getTable().getId());
        tableDto.setNumber(order.getTable().getNumber());
        orderResponseDto.setTable(tableDto);

        orderResponseDto.setStatus(order.getStatus());
        orderResponseDto.setRemark(order.getRemark());
        orderResponseDto.setTotalCents(order.getTotalCents());
        orderResponseDto.setPlacedAt(order.getPlacedAt());
        orderResponseDto.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemResponseDto> orderItemDtos = order.getOrderItems().stream().map(orderItem -> {
            OrderItemResponseDto itemDto = new OrderItemResponseDto();
            itemDto.setId(orderItem.getId());

            MenuItemResponseDto menuItemDto = new MenuItemResponseDto();
            menuItemDto.setId(orderItem.getMenuItem().getId());
            menuItemDto.setName(orderItem.getMenuItem().getName());
            menuItemDto.setDescription(orderItem.getMenuItem().getDescription());
            menuItemDto.setPriceCents(orderItem.getMenuItem().getPriceCents());
            menuItemDto.setImageUrl(orderItem.getMenuItem().getImageUrl());
            itemDto.setMenuItem(menuItemDto);

            itemDto.setQuantity(orderItem.getQuantity());
            itemDto.setUnitPrice(orderItem.getUnitPrice());
            itemDto.setLineTotal(orderItem.getLineTotal());
            return itemDto;
        }).collect(Collectors.toList());

        orderResponseDto.setOrderItems(orderItemDtos);

        return orderResponseDto;
    }
}
