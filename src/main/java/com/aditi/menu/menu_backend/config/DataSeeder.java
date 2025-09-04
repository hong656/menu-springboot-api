package com.aditi.menu.menu_backend.config;

import com.aditi.menu.menu_backend.entity.*;
import com.aditi.menu.menu_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    // --- All necessary repositories injected here ---
    @Autowired private WebSettingRepository webSettingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private PermissionGroupRepository permissionGroupRepository;
    @Autowired private PermissionDetailRepository permissionDetailRepository;
    @Autowired private PasswordEncoder passwordEncoder;


    @Override
    @Transactional // Ensures all seeding operations are atomic (all or nothing)
    public void run(String... args) throws Exception {
        System.out.println("Starting data seeding process...");
        
        // Seed each data type using its own method
        seedWebSettings();
        seedRbacData();
        
        System.out.println("Data seeding process complete.");
    }

    /**
     * Seeds the initial web settings like logo and title.
     */
    private void seedWebSettings() {
        if (webSettingRepository.count() == 0) {
            System.out.println("Seeding initial web settings data...");

            WebSetting logo = new WebSetting("logo", "/uploads/default_logo.png");
            WebSetting title = new WebSetting("title", "Aditi's Restaurant Menu");
            WebSetting mainTheme = new WebSetting("main_theme", "light");

            List<WebSetting> initialSettings = Arrays.asList(logo, title, mainTheme);
            webSettingRepository.saveAll(initialSettings);

            System.out.println("-> Web settings have been seeded.");
        } else {
            System.out.println("Web settings data already exists. Seeding skipped.");
        }
    }

    /**
     * Seeds all Role-Based Access Control (RBAC) data: Permissions, Roles, and default Users.
     */
    private void seedRbacData() {
        if (roleRepository.count() == 0 && userRepository.count() == 0) {
            System.out.println("Seeding RBAC data (Permissions, Roles, Users)...");

            // 1. Seed Permissions (The most granular level)
            Set<PermissionDetail> adminPermissions = seedPermissions();

            // 2. Seed Roles and assign permissions
            Role adminRole = createRole("ADMIN", "Administrator with all permissions", adminPermissions);
            Role userRole = createRole("USER", "Standard user with basic permissions", new HashSet<>());

            // 3. Seed default Admin and User accounts
            seedUsers(adminRole, userRole);

            System.out.println("-> RBAC data has been seeded.");
        } else {
            System.out.println("RBAC data already exists. Seeding skipped.");
        }
    }

    private Set<PermissionDetail> seedPermissions() {
        Set<PermissionDetail> permissions = new HashSet<>();

        // User Management Permissions
        PermissionGroup userManagementGroup = createPermissionGroup("User Management", 1);
        Permission userPermission = createPermission("Manage Users", userManagementGroup);
        permissions.add(createPermissionDetail("Create Users", "user:create", userPermission));
        permissions.add(createPermissionDetail("Read Users", "user:read", userPermission));
        permissions.add(createPermissionDetail("Update Users", "user:update", userPermission));
        permissions.add(createPermissionDetail("Delete Users", "user:delete", userPermission));

        // You can add more permission groups and details here
        // Example: Content Management
        // PermissionGroup contentManagementGroup = createPermissionGroup("Content Management", 2);
        // Permission contentPermission = createPermission("Manage Content", contentManagementGroup);
        // permissions.add(createPermissionDetail("Create Content", "content:create", contentPermission));
        
        return permissions;
    }

    private void seedUsers(Role adminRole, Role userRole) {
        // Create an Admin User
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("password")); // Change this in a real application
        adminUser.setEmail("admin@example.com");
        adminUser.setFullName("Admin User");
        adminUser.setStatus(1); // Active
        adminUser.setRoles(Set.of(adminRole));
        userRepository.save(adminUser);

        // Create a Standard User
        User standardUser = new User();
        standardUser.setUsername("user");
        standardUser.setPassword(passwordEncoder.encode("password"));
        standardUser.setEmail("user@example.com");
        standardUser.setFullName("Standard User");
        standardUser.setStatus(1); // Active
        standardUser.setRoles(Set.of(userRole));
        userRepository.save(standardUser);
    }

    // --- Helper methods to create and save entities, keeping the main logic clean ---

    private PermissionGroup createPermissionGroup(String name, int displayOrder) {
        PermissionGroup group = new PermissionGroup();
        group.setName(name);
        group.setDisplayOrder(displayOrder);
        return permissionGroupRepository.save(group);
    }

    private Permission createPermission(String name, PermissionGroup group) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setPermissionGroup(group);
        return permissionRepository.save(permission);
    }

    private PermissionDetail createPermissionDetail(String name, String slug, Permission permission) {
        PermissionDetail detail = new PermissionDetail();
        detail.setName(name);
        detail.setSlug(slug);
        detail.setPermission(permission);
        return permissionDetailRepository.save(detail);
    }

    private Role createRole(String name, String description, Set<PermissionDetail> permissions) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setPermissionDetails(permissions);
        return roleRepository.save(role);
    }
}