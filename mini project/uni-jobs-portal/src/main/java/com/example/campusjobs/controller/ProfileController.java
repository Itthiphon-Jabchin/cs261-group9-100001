package com.example.campusjobs.controller;

import com.example.campusjobs.model.User;
import com.example.campusjobs.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal) {

        String username = principal.getName();


        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            model.addAttribute("user", user);
        } else {
            // ถ้าไม่พบข้อมูล user ให้ส่ง object ว่างไปป้องกัน error
            model.addAttribute("user", new User());
        }

        return "profile"; 
    }
}
