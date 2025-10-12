package com.example.campusjobs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final TuAuthenticationProvider tuAuthProvider;

    public SecurityConfig(TuAuthenticationProvider tuAuthProvider) {
        this.tuAuthProvider = tuAuthProvider;
        System.out.println("--- SecurityConfig is being loaded ---"); 
    }

    @Bean
    public UserDetailsService users() {
        UserDetails teacher1 = User.withUsername("teacher1@uni.edu").password("1234").roles("TEACHER").build();
        UserDetails teacher2 = User.withUsername("teacher2@uni.edu").password("1234").roles("TEACHER").build();
        UserDetails student1 = User.withUsername("student1@uni.edu").password("1234").roles("STUDENT").build();
        UserDetails student2 = User.withUsername("student2@uni.edu").password("1234").roles("STUDENT").build();
        return new InMemoryUserDetailsManager(teacher1, teacher2, student1, student2);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(tuAuthProvider)
            .authorizeHttpRequests(auth -> auth
                // public pages
                .requestMatchers("/", "/login", "/public/**", "/css/**").permitAll()
                // feature protection
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
