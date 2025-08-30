package com.aditi.menu.menu_backend.specs;

import com.aditi.menu.menu_backend.entity.RestaurantTable;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TableSpecification {
    public Specification<RestaurantTable> getRestaurantTable(String search, Integer status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.notEqual(root.get("status"), 3));

            if (search != null && !search.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("number").as(String.class), "%" + search + "%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}