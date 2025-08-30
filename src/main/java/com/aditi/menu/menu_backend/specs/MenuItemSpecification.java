package com.aditi.menu.menu_backend.specs;

import com.aditi.menu.menu_backend.entity.MenuItem;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class MenuItemSpecification {

    public Specification<MenuItem> getMenuItems(String search, Integer status, Integer menuTypeId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Always exclude soft-deleted items
            predicates.add(criteriaBuilder.notEqual(root.get("status"), 3));

            // 2. Add search logic for NAME or DESCRIPTION
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate descriptionPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description").as(String.class)), 
                    searchPattern
                );
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            // 3. Add status filter
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // 4. Add menuType filter (by joining on the related entity's ID)
            if (menuTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("menuType").get("id"), menuTypeId));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<MenuItem> getPublicMenuItems(String search, Integer menuTypeId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. CRUCIAL: Always exclude 'Inactive' (2) and 'Deleted' (3) items
            // This creates a "status NOT IN (2, 3)" clause.
            predicates.add(root.get("status").in(List.of(2, 3)).not());

            // 2. Add search logic (reused from the other method)
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate descriptionPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description").as(String.class)), 
                    searchPattern
                );
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            // 3. Add menuType filter (reused from the other method)
            if (menuTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("menuType").get("id"), menuTypeId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}