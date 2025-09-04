package com.aditi.menu.menu_backend.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_group_id")
    private PermissionGroup permissionGroup;
    
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PermissionDetail> permissionDetails = new ArrayList<>();
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PermissionGroup getPermissionGroup() { return permissionGroup; }
    public void setPermissionGroup(PermissionGroup permissionGroup) { this.permissionGroup = permissionGroup; }
    public List<PermissionDetail> getPermissionDetails() { return permissionDetails; }
    public void setPermissionDetails(List<PermissionDetail> permissionDetails) { this.permissionDetails = permissionDetails; }
}