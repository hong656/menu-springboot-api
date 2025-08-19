package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.RestaurantTable;
import com.aditi.menu.menu_backend.service.RestaurantTableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    public RestaurantTableController(RestaurantTableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTables() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<RestaurantTable> tables = tableService.getAllTables();
            response.put("message", "Successfully retrieved all tables");
            response.put("data", tables);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving tables: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
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
    public ResponseEntity<Map<String, Object>> createTable(@RequestBody RestaurantTable table) {
        Map<String, Object> response = new HashMap<>();
        try {
            RestaurantTable createdTable = tableService.createTable(table);
            response.put("message", "Table created successfully");
            response.put("data", createdTable);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", "Error creating table: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
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