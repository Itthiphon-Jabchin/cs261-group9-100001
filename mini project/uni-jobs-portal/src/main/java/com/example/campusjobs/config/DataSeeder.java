package com.example.campusjobs.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.campusjobs.model.Job;
import com.example.campusjobs.repo.JobRepository;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedJobs(JobRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Job("Staff งานปฐมนิเทศ", "ช่วยต้อนรับน้องใหม่ ณ หอประชุม", "admin", "เหตุผลที่อยากเป็น staff คืออะไร?"));
                repo.save(new Job("จิตอาสา Big Cleaning Day", "ร่วมทำความสะอาดอาคารเรียน", "admin", "คุณสะดวกวันไหนบ้าง?"));
            }
        };
    }
}
