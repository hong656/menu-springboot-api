package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.dto.OrderItemRequestDto;
import com.aditi.menu.menu_backend.dto.OrderRequestDto;
import com.aditi.menu.menu_backend.entity.MenuItem;
import com.aditi.menu.menu_backend.entity.Order;
import com.aditi.menu.menu_backend.entity.OrderItem;
import com.aditi.menu.menu_backend.entity.RestaurantTable;
import com.aditi.menu.menu_backend.repository.MenuItemRepository;
import com.aditi.menu.menu_backend.repository.OrderRepository;
import com.aditi.menu.menu_backend.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    // This method does not need to change
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // This method does not need to change
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    // This method does not need to change
    public Order updateOrderStatus(Long orderId, Integer newStatus) {
        Order order = getOrderById(orderId);
        if (newStatus < 1 || newStatus > 4) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    /**
     * Creates a new order using a qrToken to identify the table.
     */
    @Transactional
    public Order createOrder(OrderRequestDto orderRequestDto) {
        // --- START OF CHANGES ---

        // 1. Get the qrToken from the request and find the corresponding table.
        String token = orderRequestDto.getQrToken();
        RestaurantTable table = restaurantTableRepository.findByQrToken(token)
                .orElseThrow(() -> new RuntimeException("Table not found with QR Token: " + token));

        // 2. (Good Practice) Check if the found table is active.
        if (table.getStatus() != 1) {
            throw new RuntimeException("Table " + table.getNumber() + " is currently inactive.");
        }

        // 3. Create the new Order and associate it with the found table entity.
        Order order = new Order();
        order.setTable(table); // Use the setter for the whole table object
        order.setRemark(orderRequestDto.getRemark());
        order.setStatus(1); // Default to 'pending' status

        // --- END OF CHANGES ---


        // This part of the logic remains exactly the same
        List<OrderItem> orderItems = new ArrayList<>();
        int totalCents = 0;

        for (OrderItemRequestDto itemDto : orderRequestDto.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + itemDto.getMenuItemId()));
            
            if (!menuItem.isAvailable()) {
                throw new RuntimeException("MenuItem " + menuItem.getName() + " is not available.");
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

        return orderRepository.save(order);
    }
}