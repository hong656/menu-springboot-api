package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    @EntityGraph(attributePaths = {"orderItems.menuItem", "table"})
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = {"orderItems.menuItem", "table"})
    List<Order> findByTableId(Long tableId);
}