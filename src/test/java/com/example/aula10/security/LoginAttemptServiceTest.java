package com.example.aula10.security;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    @Test
    void bloqueiaAposOLimiteEResetaNoSucesso() {
        LoginAttemptService service = new LoginAttemptService(2, Duration.ofMinutes(15));

        service.recordFailure("cliente@exemplo.com", "127.0.0.1");
        assertThat(service.isBlocked("cliente@exemplo.com", "127.0.0.1")).isFalse();

        service.recordFailure("cliente@exemplo.com", "127.0.0.1");
        assertThat(service.isBlocked("cliente@exemplo.com", "127.0.0.1")).isTrue();

        service.recordSuccess("cliente@exemplo.com", "127.0.0.1");
        assertThat(service.isBlocked("cliente@exemplo.com", "127.0.0.1")).isFalse();
    }

    @Test
    void normalizaMaiusculasDoEmail() {
        LoginAttemptService service = new LoginAttemptService(1, Duration.ofMinutes(15));
        service.recordFailure("CLIENTE@EXEMPLO.COM", "127.0.0.1");

        assertThat(service.isBlocked("cliente@exemplo.com", "127.0.0.1")).isTrue();
    }
}
