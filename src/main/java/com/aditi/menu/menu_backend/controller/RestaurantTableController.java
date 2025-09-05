package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.RestaurantTable;
import com.aditi.menu.menu_backend.service.RestaurantTableService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    public RestaurantTableController(RestaurantTableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('table:read')")
    public ResponseEntity<Map<String, Object>> getAllTables(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RestaurantTable> tablePage = tableService.getAllTables(pageable, search, status);

        Map<String, Object> response = new HashMap<>();
        response.put("items", tablePage.getContent());
        response.put("currentPage", tablePage.getNumber());
        response.put("pageSize", tablePage.getSize());
        response.put("totalItems", tablePage.getTotalElements());
        response.put("totalPages", tablePage.getTotalPages());
        response.put("isFirst", tablePage.isFirst());
        response.put("isLast", tablePage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('table:read')")
    public ResponseEntity<Map<String, Object>> getTableById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        return tableService.getTableById(id)
                .map(table -> {
                    response.put("message", "Successfully retrieved table");
                    response.put("data", table);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("message", "Table not found with id: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('table:read')")
    public ResponseEntity<Map<String, Object>> getTableId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        return tableService.getTableId(id)
        .map(table -> {
            response.put("message", "Successfully retrieved table");
            response.put("data", table);
            return ResponseEntity.ok(response);
        })
        .orElseGet(() -> {
            response.put("message", "Table not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }

    @GetMapping("/by-qr-token/{qrToken}")
    @PreAuthorize("hasAuthority('table:read')")
    public ResponseEntity<Map<String, Object>> getTableByQrToken(@PathVariable String qrToken) {
        Map<String, Object> response = new HashMap<>();
        return tableService.getTableByQrToken(qrToken)
                .map(table -> {
                    response.put("message", "Successfully retrieved table");
                    response.put("data", table);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("message", "Table not found with QR Token: " + qrToken);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping
    @PreAuthorize("hasAuthority('table:create')")
    public ResponseEntity<Map<String, Object>> createTable(@RequestBody RestaurantTable table) {
        Map<String, Object> response = new HashMap<>();
        try {
            RestaurantTable createdTable = tableService.createTable(table);
            response.put("message", "Table created successfully");
            response.put("data", createdTable);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("message", "Error creating table: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('table:update')")
    public ResponseEntity<Map<String, Object>> updateTable(@PathVariable Long id, @RequestBody RestaurantTable table) {
        Map<String, Object> response = new HashMap<>();
        try {
            RestaurantTable updatedTable = tableService.updateTable(id, table);
            response.put("message", "Table updated successfully");
            response.put("data", updatedTable);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('table:delete')")
    public ResponseEntity<Map<String, Object>> deleteTable(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            tableService.deleteTable(id);
            response.put("message", "Table deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('table:delete')")
    public ResponseEntity<Map<String, Object>> softDeleteTable(@PathVariable Long id, @RequestBody StatusUpdateDto statusUpdateDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            RestaurantTable updatedTable = tableService.softDeleteTable(id, statusUpdateDto);
            response.put("message", "Table status updated successfully");
            response.put("data", updatedTable);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}