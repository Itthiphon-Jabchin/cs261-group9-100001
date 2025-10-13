package com.example.campusjobs.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.campusjobs.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA จะสร้าง query ค้นหา user ด้วย username ให้เราเองจากชื่อเมธอดนี้
    Optional<User> findByUsername(String username);
}