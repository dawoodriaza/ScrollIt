package com.livestream.livestream_api.service;

import com.livestream.livestream_api.exception.*;
import com.livestream.livestream_api.model.User;
import com.livestream.livestream_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class Userservice {

    private final UserRepository  userRepository;

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}