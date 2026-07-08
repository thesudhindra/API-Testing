package com.playground.enterprise.scheduler.service;

import com.playground.enterprise.domain.NotificationChannel;
import com.playground.enterprise.domain.NotificationStatus;
import com.playground.enterprise.job.service.BackgroundJobService;
import com.playground.enterprise.notification.entity.NotificationEntity;
import com.playground.enterprise.notification.repository.NotificationRepository;
import com.playground.enterprise.scheduler.entity.ScheduledTaskEntity;
import com.playground.enterprise.scheduler.repository.ScheduledTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "playground.enterprise.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulerRunner {

    private static final Logger log = LoggerFactory.getLogger(SchedulerRunner.class);

    private final ScheduledTaskRepository scheduledTaskRepository;
    private final BackgroundJobService backgroundJobService;
    private final NotificationRepository notificationRepository;

    public SchedulerRunner(
            ScheduledTaskRepository scheduledTaskRepository,
            BackgroundJobService backgroundJobService,
            NotificationRepository notificationRepository) {
        this.scheduledTaskRepository = scheduledTaskRepository;
        this.backgroundJobService = backgroundJobService;
        this.notificationRepository = notificationRepository;
    }

    @Scheduled(fixedDelayString = "${playground.enterprise.scheduler.poll-interval-ms:5000}")
    @Transactional
    public void runDueTasks() {
        Instant now = Instant.now();
        List<ScheduledTaskEntity> dueTasks = scheduledTaskRepository.findByEnabledTrueAndNextRunAtBefore(
                now.plusSeconds(1));

        for (ScheduledTaskEntity task : dueTasks) {
            if (task.getNextRunAt() != null && task.getNextRunAt().isAfter(now)) {
                continue;
            }
            try {
                fireTask(task);
            } catch (Exception e) {
                log.warn("Failed to fire scheduled task {}: {}", task.getId(), e.getMessage());
            }
            task.setLastRunAt(now);
            task.setNextRunAt(now.plus(1, ChronoUnit.DAYS));
            scheduledTaskRepository.save(task);
        }
    }

    private void fireTask(ScheduledTaskEntity task) {
        String taskType = task.getTaskType();
        String tenantId = task.getTenantId() != null ? task.getTenantId() : "tenant-demo";

        if (taskType.contains("REMINDER") || taskType.contains("NOTIFICATION")) {
            createNotification(tenantId, task);
        } else if (taskType.contains("REPORT") || taskType.contains("AML") || taskType.contains("BATCH")) {
            backgroundJobService.submit(tenantId, taskType, task.getPayload());
        } else {
            backgroundJobService.submit(tenantId, taskType, task.getPayload());
        }
    }

    private void createNotification(String tenantId, ScheduledTaskEntity task) {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(UUID.randomUUID().toString());
        notification.setTenantId(tenantId);
        notification.setPartyId(extractPartyId(task.getPayload()));
        notification.setChannel(NotificationChannel.IN_APP);
        notification.setSubject("Scheduled: " + task.getTaskType());
        notification.setBody(task.getPayload() != null ? task.getPayload() : "Scheduled task executed");
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setCreatedAt(Instant.now());
        notificationRepository.save(notification);
    }

    private static String extractPartyId(String payload) {
        if (payload != null && payload.contains("partyId")) {
            int start = payload.indexOf("partyId");
            int colon = payload.indexOf(':', start);
            int quoteStart = payload.indexOf('"', colon);
            int quoteEnd = payload.indexOf('"', quoteStart + 1);
            if (quoteStart >= 0 && quoteEnd > quoteStart) {
                return payload.substring(quoteStart + 1, quoteEnd);
            }
        }
        return "party-customer-1";
    }
}
