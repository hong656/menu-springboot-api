package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.MenuType;
import com.aditi.menu.menu_backend.service.MenuTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/menu-types")
public class MenuTypeController {

    @Autowired
    private MenuTypeService menuTypeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMenuTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MenuType> menuTypePage = menuTypeService.getAllMenuTypes(pageable, search, status);

        Map<String, Object> response = new HashMap<>();
        response.put("items", menuTypePage.getContent());
        response.put("currentPage", menuTypePage.getNumber());
        response.put("pageSize", menuTypePage.getSize());
        response.put("totalItems", menuTypePage.getTotalElements());
        response.put("totalPages", menuTypePage.getTotalPages());
        response.put("isFirst", menuTypePage.isFirst());
        response.put("isLast", menuTypePage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all")
    public List<MenuType> getAllTypes() {
        return menuTypeService.getAllTypes();
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