package com.example.campusjobs.controller;

import com.example.campusjobs.repo.JobRepository;
import com.example.campusjobs.util.SecUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final JobRepository jobRepository;

    public HomeController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        if (SecUtil.hasRole("TEACHER")) {
            // อาจารย์เห็นเฉพาะงานที่ตัวเองโพสต์
            String me = SecUtil.currentUsername();
            model.addAttribute("jobs", jobRepository.findByCreatorUsernameOrderByCreatedAtDesc(me));
        } else {
            // นักศึกษา (หรือผู้ไม่ล็อกอิน) เห็นงานทั้งหมด
            model.addAttribute("jobs", jobRepository.findAll());
        }
        return "index";
    }
}
