package com.example.Jobportal.service;

import com.example.Jobportal.enums.Role;
import com.example.Jobportal.model.AuthResponse;

public interface FirebaseOAuthService {
    AuthResponse loginWithFirebase(String idToken, Role requestedRole, boolean allowCreate);
}
