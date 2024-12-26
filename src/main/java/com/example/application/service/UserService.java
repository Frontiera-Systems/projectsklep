package com.example.application.service;

import com.example.application.model.Cart;
import com.example.application.model.Role;
import com.example.application.model.User;
import com.example.application.model.UserRole;
import com.example.application.repository.RoleRepository;
import com.example.application.repository.UserRepository;
import com.example.application.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    private final RoleRepository roleRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    public boolean registerUser(User user, String roleName){
        if (repository.existsByUsername(user.getUsername())) {
            return false; // Użytkownik o podanym loginie już istnieje
        }

        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new RuntimeException("Rola nie istnieje");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Cart cart = new Cart();
        cart.setUser(user); // Powiązanie koszyka z użytkownikiem


        repository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(user);  // Przypisanie użytkownika
        userRole.setRole(role);  // Przypisanie roli
        userRoleRepository.save(userRole);

        return true;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Convert User entity to UserDetails
        List<GrantedAuthority> authorities = user.getUserRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

}
