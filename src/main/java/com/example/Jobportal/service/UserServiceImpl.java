package com.example.Jobportal.service;

import com.example.Jobportal.entity.UserEntity;
import com.example.Jobportal.enums.Role;
import com.example.Jobportal.model.User;
import com.example.Jobportal.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ── SAVE ─────────────────────────────────────────────────────────────────
    // WHY: BeanUtils.copyProperties is dropped completely.
    // The old User model had firstName/lastName/emailId — the new UserEntity
    // has fullName/email/role. BeanUtils would silently copy nothing and save
    // an empty row. We now map fields EXPLICITLY so nothing is missed.
    // rawPassword is a separate param — when you add Spring Security later,
    // just wrap it: entity.setPassword(passwordEncoder.encode(rawPassword))
    @Override
    public User saveUser(User user, String rawPassword) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A user with email " + user.getEmail() + " already exists.");
        }

        UserEntity entity = new UserEntity();
        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail());
        entity.setPassword(rawPassword);           // TODO: encode with BCrypt when Security added
        entity.setRole(user.getRole());

        UserEntity saved = userRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public User getUserById(Long id) {
        return toModel(findEntityById(id));
    }

    // ── GET BY EMAIL ─────────────────────────────────────────────────────────
    // WHY: essential for login flow — your Python AI service may also call
    // Spring to look up a candidate by email before fetching their profile.
    @Override
    public User getUserByEmail(String email) {
        UserEntity entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No user found with email: " + email));
        return toModel(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ── GET BY ROLE ──────────────────────────────────────────────────────────
    // WHY: admin dashboard needs to list all CANDIDATEs or all RECRUITERs.
    // Also useful when the Python AI batch job needs all candidates.
    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    public User updateUser(Long id, User user) {
        UserEntity entity = findEntityById(id);
        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail());
        if (user.getRole() != null) entity.setRole(user.getRole());
        return toModel(userRepository.save(entity));
    }

    @Override
    public boolean deleteUser(Long id) {
        userRepository.delete(findEntityById(id));
        return true;
    }

    // ── PRIVATE HELPERS ──────────────────────────────────────────────────────
    private UserEntity findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + id));
    }

    // WHY a toModel() helper instead of BeanUtils:
    // Relations like candidateProfile and recruiterProfile live on the entity
    // but must NOT be in the User response model (circular reference + data leak).
    // Explicit mapping gives us full control over what gets exposed.
    private User toModel(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getRole()
        );
    }
}