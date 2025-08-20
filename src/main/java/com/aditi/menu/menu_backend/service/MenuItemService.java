package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.repository.MenuItemRepository;
import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    private final String UPLOAD_DIR = "./uploads/images/";

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAllByStatusNot(3);
    }

    public MenuItem getMenuItemById(Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + id));
    }

    public MenuItem createMenuItem(MenuItem menuItem, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            menuItem.setImageUrl(imageUrl);
        }
        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateMenuItem(Integer id, String name, String description, Integer priceCents, Integer status, MultipartFile image) throws IOException {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + id));

        existingMenuItem.setName(name);
        existingMenuItem.setDescription(description);
        existingMenuItem.setPriceCents(priceCents);
        if (status != null) {
            existingMenuItem.setStatus(status);
        }

        if (image != null && !image.isEmpty()) {
            // Delete old image if it exists
            if (existingMenuItem.getImageUrl() != null && !existingMenuItem.getImageUrl().isEmpty()) {
                deleteImage(existingMenuItem.getImageUrl());
            }
            String imageUrl = saveImage(image);
            existingMenuItem.setImageUrl(imageUrl);
        }

        return menuItemRepository.save(existingMenuItem);
    }

    public void deleteMenuItem(Integer id) {
        MenuItem menuItem = getMenuItemById(id);
        if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
            try {
                deleteImage(menuItem.getImageUrl());
            } catch (IOException e) {
                // Log the error or handle it as needed
                System.err.println("Error deleting image: " + e.getMessage());
            }
        }
        menuItemRepository.deleteById(id);
    }

    @Transactional
    public MenuItem softDeleteMenuItem(Integer id, StatusUpdateDto statusUpdateDto) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id " + id));
        menuItem.setStatus(statusUpdateDto.getStatus());
        return menuItemRepository.save(menuItem);
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