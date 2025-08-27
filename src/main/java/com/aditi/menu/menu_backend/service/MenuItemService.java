package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.repository.MenuItemRepository;
import com.aditi.menu.menu_backend.repository.MenuTypeRepository;
import com.aditi.menu.menu_backend.entity.MenuType;
import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.MenuItem;
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
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuTypeRepository menuTypeRepository;

    private final String UPLOAD_DIR = "./uploads/images/";

    public Page<MenuItem> getAllMenuItems(Pageable pageable) {
        return menuItemRepository.findAllByStatusNot(3, pageable);
    }

    public List<MenuItem> getAllPublicMenuItems() {
        return menuItemRepository.findAllByStatusNotIn(List.of(2, 3));
    }

    public MenuItem getMenuItemById(Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + id));
    }

    public MenuItem createMenuItem(MenuItem menuItem, Integer menuTypeId, MultipartFile image) throws IOException {
        MenuType menuType = menuTypeRepository.findById(menuTypeId)
                .orElseThrow(() -> new RuntimeException("MenuType not found with id: " + menuTypeId));
        menuItem.setMenuType(menuType);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            menuItem.setImageUrl(imageUrl);
        }
        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateMenuItem(Integer id, String name, String description, Integer priceCents, Integer status, Integer menuTypeId, MultipartFile image) throws IOException {
        MenuItem existingMenuItem = getMenuItemById(id);
        MenuType menuType = menuTypeRepository.findById(menuTypeId)
                .orElseThrow(() -> new RuntimeException("MenuType not found with id: " + menuTypeId));

        existingMenuItem.setName(name);
        existingMenuItem.setDescription(description);
        existingMenuItem.setPriceCents(priceCents);
        existingMenuItem.setMenuType(menuType);
        if (status != null) {
            existingMenuItem.setStatus(status);
        }

        if (image != null && !image.isEmpty()) {
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