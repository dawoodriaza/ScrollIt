package com.livestream.livestream_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public class LiveStreamRequest {

    @Data
    public static class Create {
        @NotBlank(message = "Title is required")
        @Size(max = 100)
        private String title;

        @Size(max = 500)
        private String description;
    }

    @Data
    public static class Update {
        @Size(max = 100)
        private String title;
        @Size(max = 500)
        private String description;
    }
}