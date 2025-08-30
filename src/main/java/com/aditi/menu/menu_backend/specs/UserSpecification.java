package com.aditi.menu.menu_backend.specs;

import com.aditi.menu.menu_backend.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecification {

    public Specification<User> getUsers(String search, Integer status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.notEqual(root.get("status"), 3));

            if (search != null && !search.isEmpty()) {
                String searchLower = "%" + search.toLowerCase() + "%";
                Predicate searchFullName = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), searchLower);
                Predicate searchEmail = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchLower);
                predicates.add(criteriaBuilder.or(searchFullName, searchEmail));
            }

            if (status != null) {
                predicates.removeIf(p -> p.getExpressions().stream().anyMatch(e -> e.toString().contains("status")));
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
