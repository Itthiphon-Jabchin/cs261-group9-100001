package com.example.campusjobs.service;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.campusjobs.config.CustomUserDetails;
import com.example.campusjobs.model.User;
import com.example.campusjobs.repo.UserRepository;


@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (user.getPassword() == null) {
                throw new UsernameNotFoundException("User is an API user, trying next provider.");
            }

            return new CustomUserDetails(
            user.getUsername(),
            user.getDisplayNameTh(),
            user.getEmail(),
            user.getPassword(), // <-- เพิ่มรหัสผ่านที่ดึงมาจาก DB เข้าไป
            List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}