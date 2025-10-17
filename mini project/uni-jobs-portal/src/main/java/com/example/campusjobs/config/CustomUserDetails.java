package com.example.campusjobs.config; // หรือ package ที่คุณสร้างไว้

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String displayNameTh;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String displayNameTh, String email, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.displayNameTh = displayNameTh;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public String getDisplayNameTh() { return displayNameTh; }
    public String getEmail() { return email; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() {
        return password; 
    }
    @Override
    public String getUsername() { return username; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}