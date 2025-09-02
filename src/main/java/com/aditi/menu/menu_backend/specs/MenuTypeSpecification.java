package com.aditi.menu.menu_backend.specs;

import com.aditi.menu.menu_backend.entity.MenuType;
import com.aditi.menu.menu_backend.entity.MenuTypeTranslation;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Join;

@Component
public class MenuTypeSpecification {

    public Specification<MenuType> getMenuType(String search, Integer status) {
        return (root, query, criteriaBuilder) -> {
            if (query != null) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Join<MenuType, MenuTypeTranslation> translationJoin = root.join("translations");
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("name")), searchPattern));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}