package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.MenuType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuTypeRepository extends JpaRepository<MenuType, Integer> {
    List<MenuType> findAllByStatusNot(Integer status);
}