package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.MenuType;
import com.aditi.menu.menu_backend.service.MenuTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-types")
public class MenuTypeController {

    @Autowired
    private MenuTypeService menuTypeService;

    @GetMapping
    public List<MenuType> getAllMenuTypes() {
        return menuTypeService.getAllMenuTypes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuType> getMenuTypeById(@PathVariable Integer id) {
        return ResponseEntity.ok(menuTypeService.getMenuTypeById(id));
    }

    @PostMapping
    public MenuType createMenuType(@RequestBody MenuType menuType) {
        return menuTypeService.createMenuType(menuType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuType> updateMenuType(@PathVariable Integer id, @RequestBody MenuType menuTypeDetails) {
        return ResponseEntity.ok(menuTypeService.updateMenuType(id, menuTypeDetails));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MenuType> updateMenuTypeStatus(@PathVariable Integer id, @RequestBody StatusUpdateDto statusUpdateDto) {
        MenuType updatedMenuType = menuTypeService.updateMenuTypeStatus(id, statusUpdateDto);
        return ResponseEntity.ok(updatedMenuType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuType(@PathVariable Integer id) {
        menuTypeService.deleteMenuType(id);
        return ResponseEntity.noContent().build();
    }
}