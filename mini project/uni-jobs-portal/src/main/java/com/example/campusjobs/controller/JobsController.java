package com.example.campusjobs.controller;

import com.example.campusjobs.model.Application;
import com.example.campusjobs.repo.ApplicationRepository;
import com.example.campusjobs.repo.JobRepository;
import com.example.campusjobs.util.SecUtil;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/jobs")
public class JobsController {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public JobsController(JobRepository jobRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var job = jobRepository.findById(id).orElse(null);
        model.addAttribute("job", job);

        String me = SecUtil.currentUsername();
        boolean alreadyApplied = (me != null) && applicationRepository.existsByJobIdAndApplicantUsername(id, me);
        model.addAttribute("alreadyApplied", alreadyApplied);

        return "job_detail";
    }

    @PostMapping("/{id}/apply")
public String apply(@PathVariable Long id,
                    @RequestParam @NotBlank String fullName,
                    @RequestParam @NotBlank String studentId,
                    @RequestParam @NotBlank String email,
                    @RequestParam @NotBlank String phone,
                    @RequestParam @NotBlank String answerText,
                    RedirectAttributes ra) {

    var job = jobRepository.findById(id).orElse(null);
    if (job == null) {
        ra.addFlashAttribute("err", "ไม่พบบันทึกงาน");
        return "redirect:/";
    }

    String me = SecUtil.currentUsername();

    // 🛡️ ห้าม Teacher สมัครงาน (ตรวจจาก username)
    if (me != null && me.toLowerCase().contains("teacher")) {
        ra.addFlashAttribute("err", "Teacher ไม่สามารถสมัครงานได้");
        return "redirect:/jobs/" + id;
    }

    if (applicationRepository.existsByJobIdAndApplicantUsername(id, me)) {
        ra.addFlashAttribute("err", "คุณสมัครงานนี้แล้ว");
        return "redirect:/jobs/" + id;
    }

    var app = new Application(job, me, fullName, studentId, email, phone, answerText);
    applicationRepository.save(app);
    ra.addFlashAttribute("msg", "สมัครเรียบร้อย");
    return "redirect:/jobs/" + id;
}

}
