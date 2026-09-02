package com.example.aula10.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final Duration lockDuration;

    public LoginAttemptService(
            @Value("${app.security.login.max-attempts:5}") int maxAttempts,
            @Value("${app.security.login.lock-duration:15m}") Duration lockDuration) {
        this.maxAttempts = maxAttempts;
        this.lockDuration = lockDuration;
    }

    public boolean isBlocked(String username, String remoteAddress) {
        Instant now = Instant.now();
        return isKeyBlocked(userKey(username), now) || isKeyBlocked(ipKey(remoteAddress), now);
    }

    public void recordFailure(String username, String remoteAddress) {
        Instant now = Instant.now();
        registerFailure(userKey(username), now);
        registerFailure(ipKey(remoteAddress), now);
    }

    public void recordSuccess(String username, String remoteAddress) {
        attempts.remove(userKey(username));
        attempts.remove(ipKey(remoteAddress));
    }

    private void registerFailure(String key, Instant now) {
        attempts.compute(key, (ignored, previous) -> {
            if (previous == null || previous.isExpired(now)) {
                return new Attempt(1, now.plus(lockDuration));
            }
            return new Attempt(previous.failures() + 1, now.plus(lockDuration));
        });
    }

    private boolean isKeyBlocked(String key, Instant now) {
        Attempt attempt = attempts.get(key);
        if (attempt == null) {
            return false;
        }
        if (attempt.isExpired(now)) {
            attempts.remove(key, attempt);
            return false;
        }
        return attempt.failures() >= maxAttempts;
    }

    private String userKey(String username) {
        String normalized = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        return "user:" + sha256(normalized);
    }

    private String ipKey(String remoteAddress) {
        return "ip:" + (remoteAddress == null ? "unknown" : remoteAddress);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível.", exception);
        }
    }

    private record Attempt(int failures, Instant expiresAt) {
        boolean isExpired(Instant now) {
            return !now.isBefore(expiresAt);
        }
    }
}
