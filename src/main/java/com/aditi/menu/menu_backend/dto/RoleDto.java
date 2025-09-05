package com.aditi.menu.menu_backend.dto;

import com.aditi.menu.menu_backend.entity.PermissionDetail;
import com.aditi.menu.menu_backend.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RoleDto {
    private Long id;
    private String name;
    private String description;
    private int status;
    private Set<Long> permissionDetailIds;

    public RoleDto(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
        this.status = role.getStatus();
        this.permissionDetailIds = role.getPermissionDetails().stream()
                .map(PermissionDetail::getId)
                .collect(Collectors.toSet());
    }
}
