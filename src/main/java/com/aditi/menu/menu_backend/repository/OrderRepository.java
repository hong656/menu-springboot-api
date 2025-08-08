package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// This line is critical. It must specify the entity (Order) and its ID type (Long).
public interface OrderRepository extends JpaRepository<Order, Long> {
}
