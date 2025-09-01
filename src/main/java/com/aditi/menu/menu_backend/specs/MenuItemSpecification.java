package com.aditi.menu.menu_backend.specs;

import com.aditi.menu.menu_backend.entity.MenuItem;
import com.aditi.menu.menu_backend.entity.MenuItemTranslation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class MenuItemSpecification {

    public Specification<MenuItem> getMenuItems(String search, Integer status, Integer menuTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (query != null) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.notEqual(root.get("status"), 3));

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                
                Join<MenuItem, MenuItemTranslation> translationJoin = root.join("translations");

                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("name")), searchPattern);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("description")), searchPattern);
                
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (menuTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("menuType").get("id"), menuTypeId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Specification<MenuItem> getPublicMenuItems(String search, Integer menuTypeId) {
        return (root, query, criteriaBuilder) -> {
            if (query != null) {
                query.distinct(true);
            }
            
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("status"), 1));

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";

                Join<MenuItem, MenuItemTranslation> translationJoin = root.join("translations");

                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("name")), searchPattern);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("description")), searchPattern);

                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            if (menuTypeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("menuType").get("id"), menuTypeId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}