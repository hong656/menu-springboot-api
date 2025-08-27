package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.MenuItem;
import com.aditi.menu.menu_backend.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMenuItems(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItem> menuItemPage = menuItemService.getAllMenuItems(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("items", menuItemPage.getContent());
        response.put("currentPage", menuItemPage.getNumber());
        response.put("pageSize", menuItemPage.getSize());
        response.put("totalItems", menuItemPage.getTotalElements());
        response.put("totalPages", menuItemPage.getTotalPages());
        response.put("isFirst", menuItemPage.isFirst());
        response.put("isLast", menuItemPage.isLast());

        return ResponseEntity.ok(response);
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
        @RequestParam("status") Integer status,
        @RequestParam("menuTypeId") Integer menuTypeId,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPriceCents(priceCents);
        menuItem.setStatus(status);
        return menuItemService.createMenuItem(menuItem, menuTypeId, image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
        @PathVariable Integer id,
        @RequestParam("name") String name,
        @RequestParam("description") String description,
        @RequestParam("priceCents") Integer priceCents,
        @RequestParam("status") Integer status,
        @RequestParam("menuTypeId") Integer menuTypeId,
        @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, name, description, priceCents, status, menuTypeId, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> softDeleteMenuItem(@PathVariable Integer id, @RequestBody StatusUpdateDto statusUpdateDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            MenuItem updatedMenuItem = menuItemService.softDeleteMenuItem(id, statusUpdateDto);
            response.put("message", "Menu item status updated successfully");
            response.put("data", updatedMenuItem);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}