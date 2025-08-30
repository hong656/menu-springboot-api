package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.RestaurantTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@Repository 
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long>, JpaSpecificationExecutor<RestaurantTable> {

    Optional<RestaurantTable> findByNumber(Integer number);
    Optional<RestaurantTable> findByQrToken(String qrToken);
    Page<RestaurantTable> findAllByStatusNot(Integer status, Pageable pageable);
}