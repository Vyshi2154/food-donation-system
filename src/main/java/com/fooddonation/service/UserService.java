package com.fooddonation.service;

import com.fooddonation.entity.Role;
import com.fooddonation.entity.User;
import com.fooddonation.exception.ResourceNotFoundException;
import com.fooddonation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (userRepository.existsByEmail(user.getEmail().trim())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required (DONOR, NGO, or ADMIN)");
        }
        user.setEmail(user.getEmail().trim().toLowerCase());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email != null ? email.trim().toLowerCase() : "")
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
