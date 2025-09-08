package com.aditi.menu.menu_backend.config;

import com.aditi.menu.menu_backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response,                                   
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String username = null;

        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (JwtException e) {
            logger.error("JWT token processing error: " + e.getMessage());
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // --- THIS IS THE NEW, EFFICIENT LOGIC ---
            // Instead of hitting the database, we build UserDetails directly from the token.
            
            // 1. Extract all claims from the token using your existing JwtUtil method.
            Claims claims = jwtUtil.extractAllClaims(jwt);
            
            // 2. Extract the "permissions" claim.
            // We suppress warnings because claims.get() returns a raw Object.
            @SuppressWarnings("unchecked")
            List<String> permissions = claims.get("permissions", List.class);
            
            if (permissions == null) {
                permissions = new ArrayList<>(); // Ensure it's never null
            }

            // 3. Convert the list of permission strings into a collection of GrantedAuthority objects.
            Collection<? extends GrantedAuthority> authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 4. Create a UserDetails object. We don't need the password here, so it's empty.
            UserDetails userDetails = new User(username, "", authorities);

            // 5. Validate the token (checks expiration and subject).
            if (jwtUtil.validateToken(jwt, userDetails)) {
                
                // 6. Create the authentication token.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities() // Pass the authorities we extracted from the token
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 7. Set the authentication in the security context.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}