package com.aditi.menu.menu_backend.repository;

import com.aditi.menu.menu_backend.entity.Banner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer>, JpaSpecificationExecutor<Banner> {
    List<Banner> findAllByStatusNotIn(List<Integer> statuses);
    Page<Banner> findAllByStatusNot(Integer status, Pageable pageable);
}
