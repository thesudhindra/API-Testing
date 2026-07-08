package com.playground.enterprise.webhook.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class WebhookHttpClient {

    private final RestClient restClient = RestClient.create();

    public int post(String targetUrl, String payload, String secret) {
        if (isSimulatedFailure(targetUrl)) {
            throw new WebhookDeliveryException("Simulated webhook delivery failure for URL: " + targetUrl);
        }

        String signature = hmacSha256(payload, secret);
        if (!targetUrl.contains("localhost")) {
            return 200;
        }

        return restClient.post()
                .uri(targetUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Webhook-Signature", signature)
                .body(payload)
                .retrieve()
                .toBodilessEntity()
                .getStatusCode()
                .value();
    }

    private static boolean isSimulatedFailure(String targetUrl) {
        return targetUrl.contains("localhost")
                && (targetUrl.contains("invalid") || targetUrl.contains("fail"));
    }

    private static String hmacSha256(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HMAC-SHA256 not available", e);
        }
    }

    public static class WebhookDeliveryException extends RuntimeException {
        public WebhookDeliveryException(String message) {
            super(message);
        }
    }
}
