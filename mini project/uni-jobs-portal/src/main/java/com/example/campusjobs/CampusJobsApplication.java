package com.example.campusjobs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CampusJobsApplication {
    // Inject ค่าจาก application.properties เข้ามาในตัวแปร
    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;
    public static void main(String[] args) {
        SpringApplication.run(CampusJobsApplication.class, args);
    }

    // สร้าง Bean ที่จะทำงานตอนสตาร์ทแอป
    @Bean
    public CommandLineRunner printDbConfigRunner() {
        return args -> {
            System.out.println("==================================================");
            System.out.println("          DATABASE DEBUG INFO         ");
            System.out.println("Username read from properties: [" + dbUsername + "]");
            System.out.println("Password read from properties: [" + dbPassword + "]");
            System.out.println("==================================================");
        };
    }
}
