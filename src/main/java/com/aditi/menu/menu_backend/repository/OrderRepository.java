package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    @EntityGraph(attributePaths = {"orderItems.menuItem", "table"})
    @NonNull
    Optional<Order> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"orderItems.menuItem", "table"})
    List<Order> findByTableId(@NonNull Long tableId);
}