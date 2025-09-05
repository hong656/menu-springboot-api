package com.aditi.menu.menu_backend.dto;

import com.aditi.menu.menu_backend.entity.Permission;
import com.aditi.menu.menu_backend.entity.PermissionDetail;
import com.aditi.menu.menu_backend.entity.PermissionGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PermissionGroupDto {
    private Long id;
    private String name;
    private int displayOrder;
    private List<PermissionDto> permissions;

    public PermissionGroupDto(PermissionGroup group) {
        this.id = group.getId();
        this.name = group.getName();
        this.displayOrder = group.getDisplayOrder();
        this.permissions = group.getPermissions().stream()
                .sorted(Comparator.comparing(Permission::getName)) // Sort permissions alphabetically
                .map(PermissionDto::new)
                .collect(Collectors.toList());
    }
}

// --- DTO for the nested Permission ---
@Data
@NoArgsConstructor
class PermissionDto {
    private Long id;
    private Long permissionGroupId;
    private String name;
    private List<PermissionDetailDto> permissionDetails;

    public PermissionDto(Permission permission) {
        this.id = permission.getId();
        this.permissionGroupId = permission.getPermissionGroup().getId();
        this.name = permission.getName();
        this.permissionDetails = permission.getPermissionDetails().stream()
                .sorted(Comparator.comparing(PermissionDetail::getName)) // Sort details alphabetically
                .map(PermissionDetailDto::new)
                .collect(Collectors.toList());
    }
}

// --- DTO for the most granular Permission Detail ---
@Data
@NoArgsConstructor
class PermissionDetailDto {
    private Long id;
    private Long permissionId;
    private String name;
    private String slug;

    public PermissionDetailDto(PermissionDetail detail) {
        this.id = detail.getId();
        this.permissionId = detail.getPermission().getId();
        this.name = detail.getName();
        this.slug = detail.getSlug();
    }
}
