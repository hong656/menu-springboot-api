package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.dto.*;
import com.aditi.menu.menu_backend.entity.Order;
import com.aditi.menu.menu_backend.entity.RestaurantTable;
import com.aditi.menu.menu_backend.repository.OrderRepository;
import com.aditi.menu.menu_backend.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;
    private final OrderRepository orderRepository;

    public RestaurantTableService(RestaurantTableRepository tableRepository, OrderRepository orderRepository) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public Page<RestaurantTable> getAllTables(Pageable pageable) {
        return tableRepository.findAllByStatusNot(3, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<RestaurantTableResponseDto> getTableById(Long id) {
        return tableRepository.findById(id).map(this::convertToDtoWithOrders);
    }

    @Transactional(readOnly = true)
    public Optional<TableResponseDto> getTableId(Long id) {
        return tableRepository.findById(id).map(table -> {
            TableResponseDto dto = new TableResponseDto();
            dto.setId(table.getId());
            dto.setStatus(table.getStatus());
            dto.setNumber(table.getNumber());
            dto.setQr_token(table.getQrToken());
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public Optional<RestaurantTable> getTableByNumber(Integer number) {
        return tableRepository.findByNumber(number);
    }

    @Transactional(readOnly = true)
    public Optional<RestaurantTableResponseDto> getTableByQrToken(String qrToken) {
        return tableRepository.findByQrToken(qrToken).map(this::convertToDtoWithOrders);
    }

    @Transactional
    public RestaurantTable createTable(RestaurantTable table) {
        java.util.Optional<RestaurantTable> existingTableOpt = tableRepository.findByNumber(table.getNumber());

        if (existingTableOpt.isPresent()) {
            RestaurantTable existingTable = existingTableOpt.get();
            if (existingTable.getStatus() == 3) {
                tableRepository.delete(existingTable);
            } else {
                throw new IllegalStateException("An active table with number " + table.getNumber() + " already exists.");
            }
        }

        if (table.getQrToken() == null || table.getQrToken().isEmpty()) {
            table.setQrToken(generateUniqueQrToken());
        }
        if (table.getStatus() == null) {
            table.setStatus(1);
        }
        return tableRepository.save(table);
    }

    @Transactional
    public RestaurantTable updateTable(Long id, RestaurantTable updatedTable) {
        return tableRepository.findById(id).map(table -> {
            table.setNumber(updatedTable.getNumber());
            table.setStatus(updatedTable.getStatus());
            return tableRepository.save(table);
        }).orElseThrow(() -> new RuntimeException("Table not found with id " + id));
    }

    @Transactional
    public void deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new RuntimeException("Table not found with id " + id);
        }
        tableRepository.deleteById(id);
    }

    @Transactional
    public RestaurantTable softDeleteTable(Long id, StatusUpdateDto statusUpdateDto) {
        return tableRepository.findById(id).map(table -> {
            table.setStatus(statusUpdateDto.getStatus());
            return tableRepository.save(table);
        }).orElseThrow(() -> new RuntimeException("Table not found with id " + id));
    }

    private String generateUniqueQrToken() {
        String token;
        do {
            token = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        } while (tableRepository.findByQrToken(token).isPresent());
        return token;
    }

    private RestaurantTableResponseDto convertToDtoWithOrders(RestaurantTable table) {
        RestaurantTableResponseDto tableDto = new RestaurantTableResponseDto();
        tableDto.setId(table.getId());
        tableDto.setNumber(table.getNumber());

        List<Order> orders = orderRepository.findByTableId(table.getId());
        List<OrderInTableResponseDto> orderDtos = orders.stream().map(order -> {
            OrderInTableResponseDto orderDto = new OrderInTableResponseDto();
            orderDto.setId(order.getId());
            orderDto.setStatus(order.getStatus());
            orderDto.setRemark(order.getRemark());
            orderDto.setTotalCents(order.getTotalCents());
            orderDto.setPlacedAt(order.getPlacedAt());
            orderDto.setUpdatedAt(order.getUpdatedAt());

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

            orderDto.setOrderItems(orderItemDtos);
            return orderDto;
        }).collect(Collectors.toList());

        tableDto.setOrders(orderDtos);
        return tableDto;
    }
}