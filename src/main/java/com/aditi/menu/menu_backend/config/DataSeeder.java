package com.aditi.menu.menu_backend.config; // Or your chosen package for seeders

import com.aditi.menu.menu_backend.entity.*;
import com.aditi.menu.menu_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

            // 1. Create all possible permissions and store them in a map for easy lookup
            Map<String, PermissionDetail> allPermissions = seedAllPermissions();

            // 2. Define permission sets for each role
            Set<PermissionDetail> adminPermissions = new HashSet<>(allPermissions.values());

            Set<PermissionDetail> userPermissions = Stream.of(
                "menu:read", "menu-type:read", "table:read", "order:create", "order:read", "order:update"
            ).map(allPermissions::get).collect(Collectors.toSet());
            
            Set<PermissionDetail> testerPermissions = Stream.of(
                "menu:create", "menu:read", "menu:update",
                "menu-type:create", "menu-type:read", "menu-type:update",
                "table:read", "order:create", "order:read", "order:update"
            ).map(allPermissions::get).collect(Collectors.toSet());

            Set<PermissionDetail> guestPermissions = Stream.of(
                "menu:read", "menu-type:read", "table:read"
            ).map(allPermissions::get).collect(Collectors.toSet());


            // 3. Seed Roles and assign the permission sets
            Role adminRole = createRole("ADMIN", "Administrator with all permissions", adminPermissions);
            Role userRole = createRole("USER", "Standard user with basic permissions", userPermissions);
            Role testerRole = createRole("TESTER", "User with testing permissions", testerPermissions);
            Role guestRole = createRole("GUEST", "Guest user with limited permissions", guestPermissions);

            // 4. Seed default user accounts for each role
            seedUsers(adminRole, userRole, testerRole, guestRole);

            System.out.println("-> RBAC data has been seeded.");
        } else {
            System.out.println("RBAC data already exists. Seeding skipped.");
        }
    }

    private Map<String, PermissionDetail> seedAllPermissions() {
        Map<String, PermissionDetail> permissionsMap = new HashMap<>();

        // --- Authentication Group ---
        PermissionGroup authGroup = createPermissionGroup("Authentication", 1);
        createCrudPermissions(permissionsMap, "Users", "user", authGroup);
        createCrudPermissions(permissionsMap, "Permissions", "permission", authGroup);
        createCrudPermissions(permissionsMap, "Roles", "role", authGroup);

        // --- Menu Management Group ---
        PermissionGroup menuGroup = createPermissionGroup("Menu Management", 2);
        createCrudPermissions(permissionsMap, "Menu Items", "menu", menuGroup);
        createCrudPermissions(permissionsMap, "Menu Types", "menu-type", menuGroup);
        createCrudPermissions(permissionsMap, "Tables", "table", menuGroup);
        createCrudPermissions(permissionsMap, "Orders", "order", menuGroup);

        // --- Interface Settings Group ---
        PermissionGroup interfaceGroup = createPermissionGroup("Interface Settings", 3);
        createCrudPermissions(permissionsMap, "Banners", "banner", interfaceGroup);
        createCrudPermissions(permissionsMap, "General Settings", "general-setting", interfaceGroup);

        return permissionsMap;
    }

    // Helper to create all 4 CRUD permissions for a given resource
    private void createCrudPermissions(Map<String, PermissionDetail> map, String name, String slugPrefix, PermissionGroup group) {
        Permission permission = createPermission(name, group);
        map.put(slugPrefix + ":create", createPermissionDetail("Create " + name, slugPrefix + ":create", permission));
        map.put(slugPrefix + ":read",   createPermissionDetail("Read " + name,   slugPrefix + ":read",   permission));
        map.put(slugPrefix + ":update", createPermissionDetail("Update " + name, slugPrefix + ":update", permission));
        map.put(slugPrefix + ":delete", createPermissionDetail("Delete " + name, slugPrefix + ":delete", permission));
    }

    private void seedUsers(Role adminRole, Role userRole, Role testerRole, Role guestRole) {
        createUser("admin", "admin@example.com", "Admin User", "password", adminRole);
        createUser("user", "user@example.com", "Standard User", "password", userRole);
        createUser("tester", "tester@example.com", "Tester User", "password", testerRole);
        createUser("guest", "guest@example.com", "Guest User", "password", guestRole);
    }

    // --- Helper methods to create and save entities, keeping the main logic clean ---

    private void createUser(String username, String email, String fullName, String password, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(1); // Active
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }
    }

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