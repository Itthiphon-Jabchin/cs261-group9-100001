package com.example.campusjobs.controller;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.campusjobs.model.ApplicationStatus;
import com.example.campusjobs.model.Job; 
import com.example.campusjobs.model.Notification;
import com.example.campusjobs.model.User;
import com.example.campusjobs.repo.ApplicationRepository;
import com.example.campusjobs.repo.JobRepository;
import com.example.campusjobs.repo.NotificationRepository;
import com.example.campusjobs.repo.UserRepository;
import com.example.campusjobs.util.SecUtil;

import jakarta.validation.constraints.NotBlank;
@Controller
@Validated
@RequestMapping("/teacher")
public class TeacherJobController {

    @Autowired
    private final JobRepository jobRepository;

    @Autowired
    private final ApplicationRepository applicationRepository;

    @Autowired  
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public TeacherJobController(JobRepository jobRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/jobs")
    public String myJobs(Model model) {
        String me = SecUtil.currentUsername();
        model.addAttribute("jobs", jobRepository.findByCreatorUsernameOrderByCreatedAtDesc(me));
        return "teacher_jobs";
    }

    @GetMapping("/jobs/new")
    public String newJobForm() { 
        return "teacher_job_new"; 
    }

    @PostMapping("/jobs")
    public String createJob(@RequestParam("title") @NotBlank String title,
                            @RequestParam("description") @NotBlank String description,
                            @RequestParam("questionPrompt") @NotBlank String questionPrompt,
                            RedirectAttributes ra) {
        String me = SecUtil.currentUsername();
        Job job = new Job(title, description, me, questionPrompt);
        jobRepository.save(job);
        ra.addFlashAttribute("popupMsg", "Your post has been published !");
        return "redirect:/teacher/jobs";
    }

    // ✅ แก้เฉพาะตรงนี้เท่านั้น
    @GetMapping("/jobs/{id}/applications")
    public String viewApplications(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isEmpty() || !jobOpt.get().getCreatorUsername().equals(SecUtil.currentUsername())) {
            ra.addFlashAttribute("err", "ไม่มีสิทธิ์เข้าถึงงานนี้");
            return "redirect:/teacher/jobs";
        }

        var job = jobOpt.get();
        var apps = applicationRepository.findByJobIdOrderByAppliedAtDesc(id);

        var approvedApplicants = apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPROVED)
                .collect(Collectors.toList());

        model.addAttribute("job", job);
        model.addAttribute("apps", apps);
        model.addAttribute("approvedApplicants", approvedApplicants);

        return "teacher_applications";
    }

    @PostMapping("/applications/{appId}/approve")
    public String approve(@PathVariable("appId") Long appId, RedirectAttributes ra) {
        return updateStatus(appId, ApplicationStatus.APPROVED, ra);
    }

    @PostMapping("/applications/{appId}/interview")
    public String interview(@PathVariable("appId") Long appId, RedirectAttributes ra) {
        return updateStatus(appId, ApplicationStatus.INTERVIEW, ra);
    }

    @PostMapping("/applications/{appId}/reject")
    public String reject(@PathVariable("appId") Long appId, RedirectAttributes ra) {
        return updateStatus(appId, ApplicationStatus.REJECTED, ra);
    }

    private String updateStatus(Long appId, ApplicationStatus status, RedirectAttributes ra) {
        var appOpt = applicationRepository.findById(appId);
        if (appOpt.isEmpty()) {
            ra.addFlashAttribute("err", "ไม่พบใบสมัคร");
            return "redirect:/teacher/jobs";
        }

        var app = appOpt.get();
        var job = app.getJob();
        if (!job.getCreatorUsername().equals(SecUtil.currentUsername())) {
            ra.addFlashAttribute("err", "ไม่มีสิทธิ์ปรับสถานะงานนี้");
            return "redirect:/teacher/jobs";
        }

        app.setStatus(status);
        applicationRepository.save(app);
        if (status == ApplicationStatus.APPROVED) {
            try {
                // 4.1 ดึง Username (String) จาก Application
                String applicantUsername = app.getApplicantUsername();
                
                // 4.2 ค้นหา User object จาก Username
                Optional<User> userToNotify = userRepository.findByUsername(applicantUsername);

                // 4.3 ตรวจสอบว่าเจอ User
                if (userToNotify.isEmpty()) {
                    throw new Exception("ไม่พบผู้ใช้งาน " + applicantUsername);
                }

                // 4.4 ดึง ID (Long) ที่เราต้องการ
                Long applicantId = userToNotify.get().getId();

                // 4.5 สร้าง Notification
                Notification notification = new Notification();
                notification.setUserId(applicantId); // ⬅️ ใช้ ID (Long) ที่ถูกต้องแล้ว
                notification.setDescription("Congratulations! You’ve been selected for Staff " + job.getTitle() + " More details will be sent to your email.");
                notification.setLinkUrl("/student/applications" + app.getId()); // (แก้ URL ให้ถูก)
                
                // 4.6 บันทึก Notification
                notificationRepository.save(notification);

            }
            catch (Exception e) {
                // ถ้าล้มเหลว ให้แจ้งเตือน แต่ยังคงทำงานต่อไป
                // (ในทางปฏิบัติ ควรใช้ logger บันทึก error)
                ra.addFlashAttribute("err", "อัปเดตสถานะสำเร็จ แต่ส่ง Notification ล้มเหลว: " + e.getMessage());
            }
        }
        else if (status == ApplicationStatus.INTERVIEW) {
            try {
                // 4.1 ดึง Username (String) จาก Application
                String applicantUsername = app.getApplicantUsername();
                // 4.2 ค้นหา User object จาก Username
                Optional<User> userToNotify = userRepository.findByUsername(applicantUsername);
                // 4.3 ตรวจสอบว่าเจอ User
                if (userToNotify.isEmpty()) {
                    throw new Exception("ไม่พบผู้ใช้งาน " + applicantUsername);
                }
                // 4.4 ดึง ID (Long) ที่เราต้องการ
                Long applicantId = userToNotify.get().getId();
                // 4.5 สร้าง Notification
                Notification notification = new Notification();
                notification.setUserId(applicantId); // ⬅️ ใช้ ID (Long) ที่ถูกต้องแล้ว
                notification.setDescription("Congratulations! You’re shortlisted for Staff " + job.getTitle() + " check your email for interview details.");
                notification.setLinkUrl("/student/applications" + app.getId()); // (แก้ URL ให้ถูก)
                // 4.6 บันทึก Notification
                notificationRepository.save(notification);
            }
            catch (Exception e) {
                // ถ้าล้มเหลว ให้แจ้งเตือน แต่ยังคงทำงานต่อไป
                // (ในทางปฏิบัติ ควรใช้ logger บันทึก error)
                ra.addFlashAttribute("err", "อัปเดตสถานะสำเร็จ แต่ส่ง Notification ล้มเหลว: " + e.getMessage());
            }
        }
        else if (status == ApplicationStatus.REJECTED) {
            try {
                // 4.1 ดึง Username (String) จาก Application
                String applicantUsername = app.getApplicantUsername();
                // 4.2 ค้นหา User object จาก Username
                Optional<User> userToNotify = userRepository.findByUsername(applicantUsername);
                // 4.3 ตรวจสอบว่าเจอ User
                if (userToNotify.isEmpty()) {
                    throw new Exception("ไม่พบผู้ใช้งาน " + applicantUsername);
                }
                // 4.4 ดึง ID (Long) ที่เราต้องการ
                Long applicantId = userToNotify.get().getId();
                // 4.5 สร้าง Notification
                Notification notification = new Notification();
                notification.setUserId(applicantId); // ⬅️ ใช้ ID (Long) ที่ถูกต้องแล้ว
                notification.setDescription("Unfortunately, you did not pass the selection process to become a staff member. " + job.getTitle());
                notification.setLinkUrl("/student/applications" + app.getId()); // (แก้ URL ให้ถูก)
                // 4.6 บันทึก Notification
                notificationRepository.save(notification);
            }
            catch (Exception e) {
                // ถ้าล้มเหลว ให้แจ้งเตือน แต่ยังคงทำงานต่อไป
                // (ในทางปฏิบัติ ควรใช้ logger บันทึก error)
                ra.addFlashAttribute("err", "อัปเดตสถานะสำเร็จ แต่ส่ง Notification ล้มเหลว: " + e.getMessage());
            }
        }
        ra.addFlashAttribute("msg", "อัปเดตสถานะเรียบร้อย");
        return "redirect:/teacher/jobs/" + job.getId() + "/applications";
    }

    //อันนี้เพิ่มมาให้กด link ได้เฉยๆ ยังไม่ได้ใส่ logic ใดๆ
    @GetMapping("/applicant")
    public String applicantsPage() {
        return "teacher_applicant";
    }

    @GetMapping("/interview")
    public String interviewsPage() {
        return "teacher_interview";
    }

    @GetMapping("/final")
    public String finalPage() {
        return "teacher_final";
    }
}
