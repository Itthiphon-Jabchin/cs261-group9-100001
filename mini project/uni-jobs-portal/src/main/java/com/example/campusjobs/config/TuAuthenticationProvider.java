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

@Component
public class TuAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(TuAuthenticationProvider.class);

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

        log.info("TU API Response Status: {}", response.getStatusCode());
        log.info("TU API Response Body: {}", response.getBody());

        // --- [แก้ไข] ส่วนตรวจสอบเงื่อนไขที่ปลอดภัยและถูกต้อง ---
        boolean isAuthenticated = false;
        if (response.getBody() != null) {
            Object statusObject = response.getBody().get("status");
            // แปลงเป็น String แล้วค่อยเทียบ จะรองรับทั้ง Boolean true และ String "true"
            isAuthenticated = statusObject != null && statusObject.toString().equalsIgnoreCase("true");
        }

        if (response.getStatusCode() == HttpStatus.OK && isAuthenticated) {
            String type = (String) response.getBody().get("type");
            if (type == null) {
                // ถ้า API ไม่ส่ง type มาให้ ให้โยน error ที่ชัดเจน
                log.error("TU API response is successful but 'type' field is missing.");
                throw new BadCredentialsException("User type not found in API response");
            }
            String role = type.equalsIgnoreCase("student") ? "ROLE_STUDENT" : "ROLE_TEACHER";

            return new UsernamePasswordAuthenticationToken(username, password,
                    List.of(new SimpleGrantedAuthority(role)));
        } else {
            log.warn("Authentication failed. API response indicates failure or status key is missing/false.");
            throw new BadCredentialsException("Invalid TU credentials");
        }
        // ---------------------------------------------------------

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
