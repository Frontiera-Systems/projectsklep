package com.example.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.security.core.userdetails.User;
import com.example.application.model.User;
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);
}
