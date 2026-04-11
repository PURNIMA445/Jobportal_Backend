package com.example.Jobportal.service;

import com.example.Jobportal.enums.Role;
import com.example.Jobportal.model.User;

import java.util.List;

public interface UserService {

    User saveUser(User user, String rawPassword);  // rawPassword kept separate — ready for BCrypt later
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);             // needed for login
    boolean existsByEmail(String email);           // duplicate registration check
    List<User> getUsersByRole(Role role);          // admin: list all candidates / recruiters
    User updateUser(Long id, User user);
    boolean deleteUser(Long id);
}