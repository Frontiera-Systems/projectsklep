package com.example.application.service;

import com.example.application.model.Role;
import com.example.application.model.User;
import com.example.application.model.UserRole;
import com.example.application.repository.RoleRepository;
import com.example.application.repository.UserRepository;
import com.example.application.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    //private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(@Lazy UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public boolean registerUser(User user, String roleName){
        if (repository.existsByUsername(user.getUsername())) {
            return false; // Użytkownik o podanym loginie już istnieje
        }

        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new RuntimeException("Rola nie istnieje");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = repository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(user);  // Przypisanie użytkownika
        userRole.setRole(role);  // Przypisanie roli

        // Zapisanie powiązania w tabeli pośredniczącej
        userRoleRepository.save(userRole);

        return true;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username);
    }

}
