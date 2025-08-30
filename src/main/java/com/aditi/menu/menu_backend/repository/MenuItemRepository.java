package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer>, JpaSpecificationExecutor<MenuItem> {
    Page<MenuItem> findAllByStatusNot(Integer status, Pageable pageable);
    List<MenuItem> findAllByStatusNotIn(List<Integer> statuses);
}
