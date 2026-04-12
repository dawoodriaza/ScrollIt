package com.livestream.livestream_api.service;

import com.livestream.livestream_api.dto.request.UserUpdateRequest;
import com.livestream.livestream_api.dto.response.ApiResponse;
import com.livestream.livestream_api.exception.*;
import com.livestream.livestream_api.model.User;
import com.livestream.livestream_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository     userRepository;
    private final FileStorageService fileStorageService;


    public Page<ApiResponse.UserSummary> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(ApiResponse.UserSummary::from);
    }

    public ApiResponse.UserSummary getUserById(Long id) {
        return ApiResponse.UserSummary.from(findUser(id));
    }

    public ApiResponse.UserSummary getMyProfile(String email) {
        return ApiResponse.UserSummary.from(findUserByEmail(email));
    }

    @Transactional
    public ApiResponse.UserSummary updateMyProfile(String email, UserUpdateRequest req) {
        User user = findUserByEmail(email);
        if (req.getUsername() != null && !req.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(req.getUsername()))
                throw new DuplicateResourceException("Username already taken.");
            user.setUsername(req.getUsername());
        }
        return ApiResponse.UserSummary.from(userRepository.save(user));
    }

    @Transactional
    public ApiResponse.UserSummary updateProfilePicture(String email, MultipartFile file) {
        User user = findUserByEmail(email);
        if (user.getProfilePicture() != null)
            fileStorageService.deleteFile(user.getProfilePicture());
        user.setProfilePicture(fileStorageService.storeFile(file));
        return ApiResponse.UserSummary.from(userRepository.save(user));
    }

    @Transactional
    public ApiResponse.MessageResponse softDeleteUser(Long id, UserDetails adminDetails) {
        User admin  = findUserByEmail(adminDetails.getUsername());
        User target = findUser(id);
        if (target.getRole() != User.Role.ADMIN)
            throw new BadRequestException("Soft delete only applies to admin users.");
        target.setUserStatus(User.UserStatus.INACTIVE);
        userRepository.save(target);

        return new ApiResponse.MessageResponse("Admin user '" + target.getUsername() + "' deactivated.");
    }

    @Transactional
    public ApiResponse.MessageResponse deleteUser(Long id, UserDetails adminDetails) {
        User admin  = findUserByEmail(adminDetails.getUsername());
        User target = findUser(id);
        userRepository.delete(target);

        return new ApiResponse.MessageResponse("User deleted successfully.");
    }

    @Transactional
    public ApiResponse.UserSummary addCoins(Long userId, int coins, UserDetails adminDetails) {
        User user = findUser(userId);
        user.setCoinBalance(user.getCoinBalance() + coins);
        userRepository.save(user);

        return ApiResponse.UserSummary.from(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}