package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.repository.MenuItemRepository;
import com.aditi.menu.menu_backend.repository.MenuTypeRepository;
import com.aditi.menu.menu_backend.entity.MenuType;
import com.aditi.menu.menu_backend.dto.MenuItemRequestDto;
import com.aditi.menu.menu_backend.dto.MenuItemTranslationDto;
import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.MenuItem;
import com.aditi.menu.menu_backend.entity.MenuItemTranslation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.aditi.menu.menu_backend.specs.MenuItemSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.function.Function;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuTypeRepository menuTypeRepository;

    @Autowired
    private MenuItemSpecification menuItemSpecification;

    private final String UPLOAD_DIR = "./uploads/images/";
    
    public Page<MenuItem> getAllMenuItems(Pageable pageable, String search, Integer status, Integer menuTypeId) {
        Specification<MenuItem> spec = menuItemSpecification.getMenuItems(search, status, menuTypeId);
        return menuItemRepository.findAll(spec, pageable);
    }

    public List<MenuItem> getAllPublicMenuItems(String search, Integer menuTypeId) {
        Specification<MenuItem> spec = menuItemSpecification.getPublicMenuItems(search, menuTypeId);
        return menuItemRepository.findAll(spec);
    }

    public MenuItem getMenuItemById(Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + id));
    }

    public MenuItem createMenuItem(MenuItemRequestDto menuItemDto, MultipartFile image) throws IOException {
        MenuType menuType = menuTypeRepository.findById(menuItemDto.getMenuTypeId())
            .orElseThrow(() -> new RuntimeException("MenuType not found with id: " + menuItemDto.getMenuTypeId()));

        MenuItem menuItem = new MenuItem();
        menuItem.setPriceCents(menuItemDto.getPriceCents());
        menuItem.setStatus(menuItemDto.getStatus());
        menuItem.setMenuType(menuType);

        List<MenuItemTranslation> translations = menuItemDto.getTranslations().stream()
                .map(dto -> {
                    MenuItemTranslation translation = new MenuItemTranslation();
                    translation.setLanguageCode(dto.getLanguageCode());
                    translation.setName(dto.getName());
                    translation.setDescription(dto.getDescription());
                    translation.setMenuItem(menuItem);
                    return translation;
                }).collect(Collectors.toList());
        menuItem.setTranslations(translations);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            menuItem.setImageUrl(imageUrl);
        }
        return menuItemRepository.save(menuItem);
    }

    @Transactional // Add @Transactional to ensure all DB operations are in one transaction
    public MenuItem updateMenuItem(Integer id, MenuItemRequestDto menuItemDto, MultipartFile image) throws IOException {
        MenuItem existingMenuItem = getMenuItemById(id);
        MenuType menuType = menuTypeRepository.findById(menuItemDto.getMenuTypeId())
                .orElseThrow(() -> new RuntimeException("MenuType not found with id: " + menuItemDto.getMenuTypeId()));

        existingMenuItem.setPriceCents(menuItemDto.getPriceCents());
        existingMenuItem.setMenuType(menuType);
        if (menuItemDto.getStatus() != null) {
            existingMenuItem.setStatus(menuItemDto.getStatus());
        }

        // --- START OF THE FIX ---

        // 1. Create a Map of existing translations for easy lookup by language code.
        Map<String, MenuItemTranslation> existingTranslationsMap = existingMenuItem.getTranslations().stream()
                .collect(Collectors.toMap(MenuItemTranslation::getLanguageCode, Function.identity()));
        
        // 2. Process incoming translations from the request DTO.
        for (MenuItemTranslationDto translationDto : menuItemDto.getTranslations()) {
            MenuItemTranslation existingTranslation = existingTranslationsMap.get(translationDto.getLanguageCode());

            if (existingTranslation != null) {
                // 3. IF IT EXISTS: Update its name and description.
                existingTranslation.setName(translationDto.getName());
                existingTranslation.setDescription(translationDto.getDescription());
                // Remove it from the map, so we know it's been processed.
                existingTranslationsMap.remove(translationDto.getLanguageCode());
            } else {
                // 4. IF IT DOES NOT EXIST: Create a new translation and add it to the item.
                MenuItemTranslation newTranslation = new MenuItemTranslation();
                newTranslation.setLanguageCode(translationDto.getLanguageCode());
                newTranslation.setName(translationDto.getName());
                newTranslation.setDescription(translationDto.getDescription());
                newTranslation.setMenuItem(existingMenuItem); // Link it to the parent
                existingMenuItem.getTranslations().add(newTranslation);
            }
        }

        // 5. IF any translations are left in the map, it means they were not in the new data
        //    and should be removed. This handles deleting a translation.
        if (!existingTranslationsMap.isEmpty()) {
            existingMenuItem.getTranslations().removeAll(existingTranslationsMap.values());
        }

        // --- END OF THE FIX ---

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