package com.playground.enterprise.scheduler.service;

import com.playground.enterprise.scheduler.dto.RegisterTaskRequest;
import com.playground.enterprise.scheduler.dto.ScheduledTaskResponse;
import com.playground.enterprise.scheduler.entity.ScheduledTaskEntity;
import com.playground.enterprise.scheduler.repository.ScheduledTaskRepository;
import com.playground.common.dto.PageResponse;
import com.playground.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SchedulerService {

    private final ScheduledTaskRepository scheduledTaskRepository;

    public SchedulerService(ScheduledTaskRepository scheduledTaskRepository) {
        this.scheduledTaskRepository = scheduledTaskRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ScheduledTaskResponse> listTasks(String tenantId, int page, int size) {
        List<ScheduledTaskEntity> tenantTasks = scheduledTaskRepository.findAll().stream()
                .filter(task -> tenantId.equals(task.getTenantId()))
                .sorted(Comparator.comparing(ScheduledTaskEntity::getCreatedAt).reversed())
                .toList();

        int fromIndex = Math.min(page * size, tenantTasks.size());
        int toIndex = Math.min(fromIndex + size, tenantTasks.size());
        List<ScheduledTaskResponse> items = tenantTasks.subList(fromIndex, toIndex).stream()
                .map(SchedulerService::toResponse)
                .toList();

        Page<ScheduledTaskEntity> result = new PageImpl<>(
                tenantTasks.subList(fromIndex, toIndex),
                PageRequest.of(page, size),
                tenantTasks.size());

        return new PageResponse<>(
                items,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                null);
    }

    @Transactional
    public ScheduledTaskResponse registerTask(String tenantId, String taskType, String cronExpression, String payload) {
        ScheduledTaskEntity task = new ScheduledTaskEntity();
        task.setId(UUID.randomUUID().toString());
        task.setTenantId(tenantId);
        task.setTaskType(taskType);
        task.setCronExpression(cronExpression);
        task.setPayload(payload);
        task.setEnabled(true);
        task.setNextRunAt(Instant.now());
        task.setCreatedAt(Instant.now());

        return toResponse(scheduledTaskRepository.save(task));
    }

    @Transactional
    public ScheduledTaskResponse registerTask(String tenantId, RegisterTaskRequest request) {
        return registerTask(tenantId, request.taskType(), request.cronExpression(), request.payload());
    }

    @Transactional(readOnly = true)
    public ScheduledTaskResponse getTask(String tenantId, String taskId) {
        ScheduledTaskEntity task = scheduledTaskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled task not found"));
        return toResponse(task);
    }

    static ScheduledTaskResponse toResponse(ScheduledTaskEntity task) {
        return new ScheduledTaskResponse(
                task.getId(),
                task.getTenantId(),
                task.getTaskType(),
                task.getCronExpression(),
                task.getPayload(),
                task.isEnabled(),
                task.getLastRunAt(),
                task.getNextRunAt(),
                task.getCreatedAt());
    }
}
