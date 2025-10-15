package com.example.campusjobs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/jobs", "/jobs/*").permitAll()
                .requestMatchers("/login").permitAll()

                // Student-only actions
                .requestMatchers("/jobs/*/apply", "/student/**").hasRole("STUDENT")

                // Teacher-only actions
                .requestMatchers("/teacher/**").hasRole("TEACHER")

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

    /**
     * Simple in-memory users for demo/testing.
     * Replace with real user store later (JPA/JDBC).
     */
    @Bean
    public UserDetailsService userDetailsService() {
        var teacher1 = User.withUsername("teacher1@uni.edu").password("1234").roles("TEACHER").build();
        var teacher2 = User.withUsername("teacher2@uni.edu").password("1234").roles("TEACHER").build();
        var student1 = User.withUsername("student1@uni.edu").password("1234").roles("STUDENT").build();
        var student2 = User.withUsername("student2@uni.edu").password("1234").roles("STUDENT").build();
        return new InMemoryUserDetailsManager(teacher1, teacher2, student1, student2);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Demo only (plaintext). Use BCrypt in production.
        return NoOpPasswordEncoder.getInstance();
    }
}
