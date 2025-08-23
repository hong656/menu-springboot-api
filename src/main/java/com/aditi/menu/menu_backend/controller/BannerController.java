package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.Banner;
import com.aditi.menu.menu_backend.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public List<Banner> getAllBanners() {
        return bannerService.getAllBanners();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banner> getBannerById(@PathVariable Integer id) {
        return ResponseEntity.ok(bannerService.getBannerById(id));
    }

    @PostMapping
    public Banner createBanner(
        @RequestParam("title") String title,
        @RequestParam("status") Integer status,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        return bannerService.createBanner(title,status, image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Banner> updateBanner(
        @PathVariable Integer id,
        @RequestParam("title") String title,
        @RequestParam("status") Integer status,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(bannerService.updateBanner(id, title, status, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Integer id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> softDeleteBanner(@PathVariable Integer id, @RequestBody StatusUpdateDto statusUpdateDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Banner updatedBanner = bannerService.softDeleteBanner(id, statusUpdateDto);
            response.put("message", "Banner status updated successfully");
            response.put("data", updatedBanner);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
