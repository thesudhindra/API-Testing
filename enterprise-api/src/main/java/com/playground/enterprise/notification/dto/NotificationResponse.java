package com.playground.enterprise.notification.dto;

import com.playground.enterprise.domain.NotificationChannel;
import com.playground.enterprise.domain.NotificationStatus;

import java.time.Instant;

public record NotificationResponse(
        String id,
        String partyId,
        NotificationChannel channel,
        String subject,
        String body,
        NotificationStatus status,
        Instant readAt,
        Instant createdAt
) {
}
