package com.aditi.menu.menu_backend.specs;

import com.aditi.menu.menu_backend.entity.Role;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class RoleSpecification {

    public static Specification<Role> search(String keyword, Integer status) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Always exclude roles with status 3 (deleted)
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.notEqual(root.get("status"), 3));

            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern));
            }

            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }

            return predicate;
        };
    }
}
