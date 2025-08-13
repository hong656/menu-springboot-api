package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.entity.MenuItem;
import com.aditi.menu.menu_backend.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public List<MenuItem> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(menuItemService.getMenuItemById(id));
    }

    @PostMapping
    public MenuItem createMenuItem(
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("priceCents") Integer priceCents,
        @RequestParam("available") boolean available,                     
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPriceCents(priceCents);
        menuItem.setAvailable(available);
        return menuItemService.createMenuItem(menuItem, image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
        @PathVariable Integer id,                                                   
        @RequestParam("name") String name,                                                   
        @RequestParam("description") String description,                                                   
        @RequestParam("priceCents") Integer priceCents,                                                  
        @RequestParam("available") boolean available,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPriceCents(priceCents);
        menuItem.setAvailable(available);
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, menuItem, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
