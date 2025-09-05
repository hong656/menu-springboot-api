package com.aditi.menu.menu_backend.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UpdateRolePermissionsRequest {
    private Set<Long> permissionDetailIds;
}