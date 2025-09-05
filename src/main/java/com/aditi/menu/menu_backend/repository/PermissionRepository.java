package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}