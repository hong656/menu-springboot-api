package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.MenuType;
import com.aditi.menu.menu_backend.repository.MenuTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuTypeService {

    @Autowired
    private MenuTypeRepository menuTypeRepository;

    public List<MenuType> getAllMenuTypes() {
        // Return only active and inactive types, not deleted ones
        return menuTypeRepository.findAllByStatusNot(3);
    }

    public MenuType getMenuTypeById(Integer id) {
        return menuTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuType not found with id: " + id));
    }

    public MenuType createMenuType(MenuType menuType) {
        // You can add validation here, e.g., check if name already exists
        return menuTypeRepository.save(menuType);
    }

    public MenuType updateMenuType(Integer id, MenuType menuTypeDetails) {
        MenuType existingMenuType = getMenuTypeById(id);
        existingMenuType.setName(menuTypeDetails.getName());
        // Status is updated via a separate PATCH endpoint
        return menuTypeRepository.save(existingMenuType);
    }

    public MenuType updateMenuTypeStatus(Integer id, StatusUpdateDto statusUpdateDto) {
        MenuType menuType = getMenuTypeById(id);
        menuType.setStatus(statusUpdateDto.getStatus());
        return menuTypeRepository.save(menuType);
    }

    // A hard delete - generally not recommended if you have a status field
    public void deleteMenuType(Integer id) {
        if (!menuTypeRepository.existsById(id)) {
            throw new RuntimeException("MenuType not found with id: " + id);
        }
        menuTypeRepository.deleteById(id);
    }
}