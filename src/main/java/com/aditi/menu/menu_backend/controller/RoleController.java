package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.*;
import com.aditi.menu.menu_backend.dto.SimpleRoleDto;
import com.aditi.menu.menu_backend.entity.PermissionDetail;
import com.aditi.menu.menu_backend.entity.Role;
import com.aditi.menu.menu_backend.repository.PermissionDetailRepository;
import com.aditi.menu.menu_backend.repository.PermissionGroupRepository;
import com.aditi.menu.menu_backend.repository.RoleRepository;
import com.aditi.menu.menu_backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {

    @Autowired private RoleService roleService;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionGroupRepository permissionGroupRepository;
    @Autowired private PermissionDetailRepository permissionDetailRepository;

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<List<PermissionGroupDto>> getAllPermissions() {
        // Fetch all permission groups and convert them to DTOs
        List<PermissionGroupDto> permissionGroups = permissionGroupRepository.findAll().stream()
                .sorted(Comparator.comparing(com.aditi.menu.menu_backend.entity.PermissionGroup::getDisplayOrder))
                .map(PermissionGroupDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionGroups);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<Map<String, Object>> getAllRoles(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Integer status) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Role> rolePage = roleService.getAllRoles(pageable, search, status);

        Map<String, Object> response = new HashMap<>();
        response.put("items", rolePage.getContent().stream().map(RoleDto::new).collect(Collectors.toList()));
        response.put("currentPage", rolePage.getNumber());
        response.put("pageSize", rolePage.getSize());
        response.put("totalItems", rolePage.getTotalElements());
        response.put("totalPages", rolePage.getTotalPages());
        response.put("isFirst", rolePage.isFirst());
        response.put("isLast", rolePage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(role -> ResponseEntity.ok(new RoleDto(role)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleRequestDto roleRequestDto) {
        Role role = roleService.createRole(roleRequestDto);
        return ResponseEntity.ok(new RoleDto(role));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id, @RequestBody RoleRequestDto roleRequestDto) {
        Role role = roleService.updateRole(id, roleRequestDto);
        return ResponseEntity.ok(new RoleDto(role));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Map<String, Object>> softDeleteRole(@PathVariable Long id, @RequestBody StatusUpdateDto statusUpdateDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Role updatedRole = roleService.softDeleteRole(id, statusUpdateDto);
            response.put("message", "Role status updated successfully");
            response.put("data", new RoleDto(updatedRole));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    @Transactional
    public ResponseEntity<Void> updateRolePermissions(@PathVariable Long id, @RequestBody UpdateRolePermissionsRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));

        Set<PermissionDetail> newPermissions = new HashSet<>(
            permissionDetailRepository.findAllById(request.getPermissionDetailIds())
        );

        role.setPermissionDetails(newPermissions);
        roleRepository.save(role);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<List<SimpleRoleDto>> getAllRolesForSelection() {
        List<Role> roles = roleRepository.findAll();
        List<SimpleRoleDto> roleDtos = roles.stream().map(SimpleRoleDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(roleDtos);
    }
}