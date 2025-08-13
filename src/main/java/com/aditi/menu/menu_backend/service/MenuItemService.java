package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.repository.MenuItemRepository;
import com.aditi.menu.menu_backend.entity.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public MenuItem getMenuItemById(Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + id));
    }

    public MenuItem createMenuItem(MenuItem menuItem) {
        // You can add validation logic here
        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateMenuItem(Integer id, MenuItem updatedMenuItem) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("MenuItem not found with id: " + id));

        existingMenuItem.setName(updatedMenuItem.getName());
        existingMenuItem.setDescription(updatedMenuItem.getDescription());
        existingMenuItem.setPriceCents(updatedMenuItem.getPriceCents());
        existingMenuItem.setImageUrl(updatedMenuItem.getImageUrl());
        existingMenuItem.setAvailable(updatedMenuItem.isAvailable());

        return menuItemRepository.save(existingMenuItem);
    }
}
