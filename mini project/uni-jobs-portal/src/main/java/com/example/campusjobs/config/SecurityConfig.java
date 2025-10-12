package com.example.campusjobs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final TuAuthenticationProvider tuAuthProvider;

    public SecurityConfig(TuAuthenticationProvider tuAuthProvider) {
        this.tuAuthProvider = tuAuthProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(tuAuthProvider)
            .authorizeHttpRequests(auth -> auth
                // public pages
                .requestMatchers("/", "/login", "/public/**", "/css/**").permitAll()
                // feature protection
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/jobs/*/apply").hasRole("STUDENT")
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
