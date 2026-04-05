package com.livestream.livestream_api.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CommentRequest {

    @Data
    public static class Create {
        @NotBlank(message = "Message cannot be empty")
        @Size(max = 1000)
        private String message;
    }

    @Data
    public static class Update {
        @NotBlank(message = "Message cannot be empty")
        @Size(max = 1000)
        private String message;
    }
}