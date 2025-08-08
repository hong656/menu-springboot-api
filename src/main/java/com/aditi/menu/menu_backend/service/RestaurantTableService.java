package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.entity.RestaurantTable;
import com.aditi.menu.menu_backend.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // For generating QR token

@Service // Marks this class as a Spring service
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    // @Autowired // Removed the unnecessary @Autowired annotation
    public RestaurantTableService(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<RestaurantTable> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<RestaurantTable> getTableByNumber(Integer number) {
        return tableRepository.findByNumber(number);
    }

    @Transactional(readOnly = true)
    public Optional<RestaurantTable> getTableByQrToken(String qrToken) {
        return tableRepository.findByQrToken(qrToken);
    }

    @Transactional // Transactional for modifying data
    public RestaurantTable createTable(RestaurantTable table) {
        // Generate a unique QR token if not provided
        if (table.getQrToken() == null || table.getQrToken().isEmpty()) {
            table.setQrToken(generateUniqueQrToken());
        }
        // Ensure status is set, even if not explicitly provided by client
        if (table.getStatus() == null) {
            table.setStatus(1);
        }
        return tableRepository.save(table);
    }

    @Transactional
    public RestaurantTable updateTable(Long id, RestaurantTable updatedTable) {
        return tableRepository.findById(id).map(table -> {
            table.setNumber(updatedTable.getNumber());
            // QR token should generally not be updated after creation, but if needed:
            // table.setQrToken(updatedTable.getQrToken());
            table.setStatus(updatedTable.getStatus());
            return tableRepository.save(table);
        }).orElseThrow(() -> new RuntimeException("Table not found with id " + id));
    }

    @Transactional
    public void deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new RuntimeException("Table not found with id " + id);
        }
        tableRepository.deleteById(id);
    }

    // Helper method to generate a unique QR token
    private String generateUniqueQrToken() {
        String token;
        do {
            token = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
        } while (tableRepository.findByQrToken(token).isPresent()); // Ensure uniqueness
        return token;
    }
}
