package com.roombooking.api.service;

import com.roombooking.api.dto.request.LoginRequest;
import com.roombooking.api.dto.response.AuthResponse;

public interface AuthService {

     AuthResponse login(LoginRequest request);
}
