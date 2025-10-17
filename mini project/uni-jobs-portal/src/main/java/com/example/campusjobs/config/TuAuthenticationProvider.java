package com.example.campusjobs.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.campusjobs.model.User;
import com.example.campusjobs.service.UserService;

@Component
public class TuAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(TuAuthenticationProvider.class);

    @Value("${tu.api.url:https://restapi.tu.ac.th/api/v1/auth/Ad/verify}")
    private String tuApiUrl;
    @Value("${tu.api.key}")
    private String apiKey;

    private final UserService userService;

    public TuAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("UserName", username);
        requestBody.put("PassWord", password);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Application-Key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tuApiUrl, request, Map.class);

            boolean isAuthenticated = false;
            if (response.getBody() != null) {
                Object statusObject = response.getBody().get("status");
                isAuthenticated = statusObject != null && statusObject.toString().equalsIgnoreCase("true");
            }

            if (response.getStatusCode() == HttpStatus.OK && isAuthenticated) {
                
                User user = userService.findOrCreateUser(response.getBody());

                var authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

                CustomUserDetails userDetails = new CustomUserDetails(
                    user.getUsername(),
                    user.getDisplayNameTh(),
                    user.getEmail(),
                    user.getPassword(),
                    authorities
                );
                
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            } else {
                log.warn("Authentication failed. API response indicates failure.");
                throw new BadCredentialsException("Invalid TU credentials");
            }
        } catch (Exception e) {
            log.error("Exception while calling TU API", e);
            throw new BadCredentialsException("Cannot verify TU credentials", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}