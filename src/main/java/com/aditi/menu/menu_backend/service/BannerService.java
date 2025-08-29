package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.Banner;
import com.aditi.menu.menu_backend.repository.BannerRepository;
import com.aditi.menu.menu_backend.specs.BannerSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private BannerSpecification bannerSpecification;

    private final String UPLOAD_DIR = "./uploads/images/";

    public Page<Banner> getAllBanners(Pageable pageable, String search, Integer status) {
        Specification<Banner> spec = bannerSpecification.getBanners(search, status);
        return bannerRepository.findAll(spec, pageable);
    }

    public List<Banner> getAllPublicBanners() {
        return bannerRepository.findAllByStatusNotIn(List.of(2, 3));
    }

    public Banner getBannerById(Integer id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));
    }

    public Banner createBanner(String title,Integer status, MultipartFile image) throws IOException {
        Banner banner = new Banner();
        banner.setTitle(title);
        if (status != null) {
            banner.setStatus(status);
        }
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            banner.setBannerImage(imageUrl);
        }
        return bannerRepository.save(banner);
    }

    public Banner updateBanner(Integer id, String title, Integer status, MultipartFile image) throws IOException {
        Banner existingBanner = bannerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Banner not found with id: " + id));

        existingBanner.setTitle(title);
        if (status != null) {
            existingBanner.setStatus(status);
        }

        if (image != null && !image.isEmpty()) {
            // Delete old image if it exists
            if (existingBanner.getBannerImage() != null && !existingBanner.getBannerImage().isEmpty()) {
                deleteImage(existingBanner.getBannerImage());
            }
            String imageUrl = saveImage(image);
            existingBanner.setBannerImage(imageUrl);
        }

        return bannerRepository.save(existingBanner);
    }

    public void deleteBanner(Integer id) {
        Banner banner = getBannerById(id);
        if (banner.getBannerImage() != null && !banner.getBannerImage().isEmpty()) {
            try {
                deleteImage(banner.getBannerImage());
            } catch (IOException e) {
                System.err.println("Error deleting image: " + e.getMessage());
            }
        }
        bannerRepository.deleteById(id);
    }

    @Transactional
    public Banner softDeleteBanner(Integer id, StatusUpdateDto statusUpdateDto) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with id " + id));
        banner.setStatus(statusUpdateDto.getStatus());
        return bannerRepository.save(banner);
    }

    private String saveImage(MultipartFile image) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath);

        return "/images/" + fileName;
    }

    private void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || !imageUrl.startsWith("/images/")) {
            return;
        }
        String filename = imageUrl.substring("/images/".length());
        Path imagePath = Paths.get(UPLOAD_DIR, filename);
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }
    }
}
