package com.roombooking.api.service.impl;

import com.roombooking.api.dto.request.UserRequest;
import com.roombooking.api.entity.User;
import com.roombooking.api.exception.BusinessRuleException;
import com.roombooking.api.repository.UserRepository;
import com.roombooking.api.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException(
                    "Email is already registered"
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setEnabled(true);

        userRepository.save(user);
    }

    
}
