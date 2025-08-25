package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.WebSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebSettingRepository extends JpaRepository<WebSetting, String> {
}