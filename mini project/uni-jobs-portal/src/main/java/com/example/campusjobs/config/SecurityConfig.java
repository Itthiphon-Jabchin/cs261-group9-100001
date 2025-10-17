package com.example.campusjobs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.campusjobs.service.DatabaseUserDetailsService;

@Configuration
public class SecurityConfig {

    private final TuAuthenticationProvider tuAuthProvider;
    private final DatabaseUserDetailsService dbUserDetailsService; // 1. Inject Service ที่คุยกับ DB ของเรา

    public SecurityConfig(TuAuthenticationProvider tuAuthProvider, DatabaseUserDetailsService dbUserDetailsService) {
        this.tuAuthProvider = tuAuthProvider;
        this.dbUserDetailsService = dbUserDetailsService;
    }

    // 3. สร้าง "ผู้จัดการ" ที่ดูแลยาม 2 คน
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        // สร้าง "ยาม" สำหรับ user ในระบบ (ทั้ง admin และ user ใน DB)
        // ยามคนนี้จะไปเรียกใช้ DatabaseUserDetailsService ของเรา
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        daoAuthProvider.setUserDetailsService(dbUserDetailsService);
        daoAuthProvider.setPasswordEncoder(passwordEncoder);
        
        // ProviderManager จะถาม `daoAuthProvider` (ที่ดู DB) ก่อน
        // ถ้าหา user ไม่เจอ (UsernameNotFoundException) ถึงจะไปถาม `tuAuthProvider` (ยาม API) ต่อ
        return new ProviderManager(daoAuthProvider, tuAuthProvider);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .authenticationManager(authenticationManager) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/", "/login", "/public/**", "/css/**").permitAll()
                .requestMatchers("/teacher/**").hasAuthority("ROLE_TEACHER")
                .requestMatchers("/student/**").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/jobs/*/apply").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/jobs/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
            .csrf(Customizer.withDefaults());

        return http.build();
    }
}