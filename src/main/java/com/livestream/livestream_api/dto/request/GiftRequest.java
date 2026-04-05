package com.livestream.livestream_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

public class GiftRequest {

    @Data
    public static class Create {
        @NotBlank(message = "Gift name is required")
        @Size(max = 50)
        private String giftName;
        @Min(value = 1, message = "Coin value must be at least 1")
        private int coinValue;
        private String iconUrl;
    }

    @Data
    public static class Update {
        @Size(max = 50)
        private String giftName;
        @Min(value = 1)
        private int coinValue;
        private String iconUrl;
        private Boolean active;
    }

    @Data
    public static class Send {
        @NotNull(message = "Gift ID is required")
        private Long giftId;
    }
}