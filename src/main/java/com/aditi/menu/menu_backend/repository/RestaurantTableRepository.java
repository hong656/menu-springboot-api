package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.RestaurantTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marks this interface as a Spring Data repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    // Custom method to find a table by its number
    Optional<RestaurantTable> findByNumber(Integer number);

    // Custom method to find a table by its QR token
    Optional<RestaurantTable> findByQrToken(String qrToken);

    Page<RestaurantTable> findAllByStatusNot(Integer status, Pageable pageable);
}
