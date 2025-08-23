package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {
    List<Banner> findAllByStatusNotIn(List<Integer> statuses);
    List<Banner> findAllByStatusNot(Integer status);
}
