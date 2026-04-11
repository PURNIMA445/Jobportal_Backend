package com.example.Jobportal.controller;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.model.User;
import com.example.Jobportal.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
import com.example.Jobportal.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;
    private final UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public UserController(UserService userService) {

        this.userService = userService;
    }
    @PostMapping("/users")
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user, user.getPassword()); // now matches interface
    }
    @GetMapping("/users")
    public List<User> getAllUsers()
    {
        return userService.getAllUsers();
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id)
    {
        User user;
        user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    @DeleteMapping("/users/{id}")
    public  ResponseEntity<Map<String,Boolean>> deleteEmployee(@PathVariable Long id)
    {
        boolean deleted;
        deleted=userService.deleteUser(id);
        Map<String,Boolean> response= new HashMap<>();
        response.put("deleted",deleted);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDto) {
        UserEntity updatedEntity = userService.updateUser(id, userDto);

        User updatedUser = new User();
        updatedUser.setId(updatedEntity.getId());
        updatedUser.setFirstName(updatedEntity.getFirstName());
        updatedUser.setLastName(updatedEntity.getLastName());
        updatedUser.setEmailId(updatedEntity.getEmailId());

        return ResponseEntity.ok(updatedUser);
    }
}
