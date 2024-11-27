package com.example.application.repository;

import com.example.application.model.User;
import com.example.application.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    // Możesz dodać metody wyszukiwania, np. po user_id lub role_id
    List<UserRole> findByUser(User user);

}