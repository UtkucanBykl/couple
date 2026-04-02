package com.example.couple.security;

import com.example.couple.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;

    public static CustomUserPrincipal fromEntity(User user) {
        return new CustomUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}