package com.security.fiverr.user.service;

import com.security.fiverr.security.userdetails.UserDetailsImpl;
import com.security.fiverr.user.model.enitity.ERole;
import com.security.fiverr.user.model.enitity.Role;
import com.security.fiverr.user.model.enitity.User;
import com.security.fiverr.user.repository.RoleRepository;
import com.security.fiverr.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public Role getDefaultRole() {
        return roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Default role is not found."));
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserFromUserDetails(UserDetailsImpl userDetails) {
        return findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getEmail()));
    }
}