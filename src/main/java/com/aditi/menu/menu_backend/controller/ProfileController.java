package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.entity.User;
import com.aditi.menu.menu_backend.repository.UserRepository;
import com.aditi.menu.menu_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User currentUser = user.get();
            UserProfile profile = new UserProfile(
                currentUser.getId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getFullName(),
                currentUser.getRole(),
                currentUser.getStatus()
            );
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.getAllUsers(pageable, search, status);

        Page<UserProfile> userProfilePage = userPage.map(user -> new UserProfile(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getStatus()));

        Map<String, Object> response = new HashMap<>();
        response.put("items", userProfilePage.getContent());
        response.put("currentPage", userProfilePage.getNumber());
        response.put("pageSize", userProfilePage.getSize());
        response.put("totalItems", userProfilePage.getTotalElements());
        response.put("totalPages", userProfilePage.getTotalPages());
        response.put("isFirst", userProfilePage.isFirst());
        response.put("isLast", userProfilePage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/token-info")
    public ResponseEntity<?> getTokenInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        TokenInfo tokenInfo = new TokenInfo(
            authentication.getName(),
            authentication.getAuthorities().toString(),
            authentication.isAuthenticated()
        );
        
        return ResponseEntity.ok(tokenInfo);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (request.getFullName() != null) {
                user.setFullName(request.getFullName());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            
            User updatedUser = userRepository.save(user);
            
            UserProfile profile = new UserProfile(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getFullName(),
                updatedUser.getRole(),
                updatedUser.getStatus()
            );
            
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (request.getUsername() != null) {
                user.setUsername(request.getUsername());
            }
            if (request.getPassword() != null) {
                user.setPassword(encoder.encode(request.getPassword()));
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getRole() != null) {
                user.setRole(request.getRole());
            }
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }

            User updatedUser = userRepository.save(user);

            UserProfile profile = new UserProfile(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getFullName(),
                updatedUser.getRole(),
                updatedUser.getStatus()
            );

            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/users/delete/{id}")
    public ResponseEntity<?> softDeleteUser(@PathVariable Long id, @RequestBody DeleteUserRequest request) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (request.getStatus() != null && request.getStatus() == 3) {
                user.setStatus(request.getStatus());
                User updatedUser = userRepository.save(user);

                UserProfile profile = new UserProfile(
                    updatedUser.getId(),
                    updatedUser.getUsername(),
                    updatedUser.getEmail(),
                    updatedUser.getFullName(),
                    updatedUser.getRole(),
                    updatedUser.getStatus()
                );

                return ResponseEntity.ok(profile);
            } else {
                return ResponseEntity.badRequest().body("Invalid status provided for soft delete.");
            }
        }
        return ResponseEntity.notFound().build();
    }
}

// DTOs for Profile
class UserProfile {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private int role;
    private int status;

    public UserProfile(Long id, String username, String email, String fullName, int role, int status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public int getRole() { return role; }
    public int getStatus() { return status; }
}

class TokenInfo {
    private String username;
    private String authorities;
    private boolean authenticated;

    public TokenInfo(String username, String authorities, boolean authenticated) {
        this.username = username;
        this.authorities = authorities;
        this.authenticated = authenticated;
    }

    // Getters
    public String getUsername() { return username; }
    public String getAuthorities() { return authorities; }
    public boolean isAuthenticated() { return authenticated; }
}

class UpdateProfileRequest {
    private String fullName;
    private String email;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

class UpdateUserRequest {
    private String username;
    private String password;
    private String email;
    private Integer role;
    private Integer status;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getRole() { return role; }
    public void setRole(Integer role) { this.role = role; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

class DeleteUserRequest {
    private Integer status;

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
