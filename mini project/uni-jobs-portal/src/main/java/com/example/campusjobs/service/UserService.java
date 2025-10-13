package com.example.campusjobs.service;

import com.example.campusjobs.model.User;
import com.example.campusjobs.repo.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrCreateUser(Map<String, Object> apiData) {
        String username = apiData.get("username").toString();

        // ค้นหาใน DB ของเราก่อน ถ้าเจอให้ return กลับไปเลย
        // ถ้าไม่เจอ ให้สร้าง User ใหม่, บันทึก, แล้วค่อย return กลับไป
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setDisplayNameTh(apiData.get("displayname_th").toString());
                    newUser.setEmail(apiData.get("email").toString());
                    
                    String type = apiData.get("type").toString();
                    String role = type.equalsIgnoreCase("student") ? "ROLE_STUDENT" : "ROLE_TEACHER";
                    newUser.setRole(role);

                    return userRepository.save(newUser);
                });
    }
}