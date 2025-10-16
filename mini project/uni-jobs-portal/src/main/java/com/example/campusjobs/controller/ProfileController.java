package com.example.campusjobs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String profilePage() {
        return "profile"; // ชื่อไฟล์ HTML (ไม่ต้องใส่ .html)
    }
}
