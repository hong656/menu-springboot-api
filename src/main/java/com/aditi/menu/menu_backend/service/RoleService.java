package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.dto.RoleRequestDto;
import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.Role;
import com.aditi.menu.menu_backend.repository.RoleRepository;
import com.aditi.menu.menu_backend.specs.RoleSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Page<Role> getAllRoles(Pageable pageable, String search, Integer status) {
        Specification<Role> spec = RoleSpecification.search(search, status);
        return roleRepository.findAll(spec, pageable);
    }

    public Role createRole(RoleRequestDto roleRequestDto) {
        Role role = new Role();
        role.setName(roleRequestDto.getName());
        role.setDescription(roleRequestDto.getDescription());
        role.setStatus(roleRequestDto.getStatus());
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, RoleRequestDto roleRequestDto) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        role.setName(roleRequestDto.getName());
        role.setDescription(roleRequestDto.getDescription());
        role.setStatus(roleRequestDto.getStatus());
        return roleRepository.save(role);
    }

    public Role softDeleteRole(Long id, StatusUpdateDto statusUpdateDto) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        role.setStatus(statusUpdateDto.getStatus());
        return roleRepository.save(role);
    }
}
