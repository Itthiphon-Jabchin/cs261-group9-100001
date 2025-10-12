package com.example.campusjobs.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@Component
public class TuAuthenticationProvider implements AuthenticationProvider {

    @Value("${tu.api.url:https://restapi.tu.ac.th/api/v1/auth/Ad/verify}")
    private String tuApiUrl;

    @Value("${tu.api.key}")  // ใช้ api-key ใน application.properties
    private String apiKey;

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

            if (response.getStatusCode() == HttpStatus.OK && (Boolean) response.getBody().get("auth")) {
                String type = (String) response.getBody().get("type");                            // ดึงข้อมูล role จาก TU-API
                String role = type.equalsIgnoreCase("student") ? "ROLE_STUDENT" : "ROLE_TEACHER"; // กำหนด role ตาม code เดิมเพราะเดี๋ยวแก้เยอะ

                return new UsernamePasswordAuthenticationToken(username, password, 
                        List.of(new SimpleGrantedAuthority(role)));
            } else {
                throw new BadCredentialsException("Invalid TU credentials");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Cannot verify TU credentials", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
