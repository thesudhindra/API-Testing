package com.playground.enterprise.notification.dto;

import com.playground.enterprise.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(
        @NotBlank String partyId,
        @NotNull NotificationChannel channel,
        @NotBlank @Size(max = 256) String subject,
        @NotBlank @Size(max = 2048) String body
) {
}
