package com.playground.enterprise.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSettingRequest(
        @NotBlank @Size(max = 1024) String settingValue
) {
}
