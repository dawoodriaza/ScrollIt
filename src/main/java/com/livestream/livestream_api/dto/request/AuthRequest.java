package com.livestream.livestream_api.dto.request;


import jakarta.validation.constraints.*;

import lombok.Data;

public class AuthRequest {

    @Data
    public static class Register {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30)
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
    }

    @Data
    public static class Login {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class ForgotPassword {
        @NotBlank @Email
        private String email;
    }

    @Data
    public static class ResetPassword {
        @NotBlank
        private String token;
        @NotBlank @Size(min = 6)
        private String newPassword;
    }

    @Data
    public static class ChangePassword {
        @NotBlank
        private String currentPassword;
        @NotBlank @Size(min = 6)
        private String newPassword;
    }
}
