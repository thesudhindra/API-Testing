package com.playground.common.web;

import org.springframework.boot.info.BuildProperties;

import java.util.LinkedHashMap;
import java.util.Map;

public final class VersionSupport {

    private VersionSupport() {
    }

    public static Map<String, Object> buildResponse(
            BuildProperties buildProperties,
            String fallbackName,
            String fallbackVersion) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (buildProperties != null) {
            body.put("name", buildProperties.getName());
            body.put("version", buildProperties.getVersion());
            body.put("artifact", buildProperties.getArtifact());
            body.put("group", buildProperties.getGroup());
            body.put("time", buildProperties.getTime() == null ? "" : buildProperties.getTime().toString());
        } else {
            body.put("name", fallbackName);
            body.put("version", fallbackVersion);
        }
        return body;
    }
}
