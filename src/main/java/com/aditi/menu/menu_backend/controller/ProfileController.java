package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.entity.User;
import com.aditi.menu.menu_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
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
                currentUser.getRole().toString(),
                currentUser.isEnabled()
            );
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
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
                updatedUser.getRole().toString(),
                updatedUser.isEnabled()
            );
            
            return ResponseEntity.ok(profile);
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
    private String role;
    private boolean enabled;

    public UserProfile(Long id, String username, String email, String fullName, String role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.enabled = enabled;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isEnabled() { return enabled; }
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