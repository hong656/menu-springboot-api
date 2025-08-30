package com.aditi.menu.menu_backend.specs;

import com.aditi.menu.menu_backend.entity.Order;
import com.aditi.menu.menu_backend.entity.RestaurantTable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderSpecification {
    public Specification<Order> getOrders(String search, Integer status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                Join<Order, RestaurantTable> tableJoin = root.join("table");
                predicates.add(criteriaBuilder.like(tableJoin.get("number").as(String.class), "%" + search + "%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
