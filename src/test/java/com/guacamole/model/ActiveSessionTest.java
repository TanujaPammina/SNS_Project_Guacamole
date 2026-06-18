package com.guacamole.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ActiveSession Model Tests")
class ActiveSessionTest {

    @Test
    @DisplayName("getDurationFormatted returns HH:MM:SS for positive seconds")
    void getDurationFormatted_positiveSeconds() {
        ActiveSession s = new ActiveSession();
        s.setDurationSeconds(3661); // 1h 1m 1s
        assertEquals("01:01:01", s.getDurationFormatted());
    }

    @Test
    @DisplayName("getDurationFormatted returns Active for negative duration")
    void getDurationFormatted_negativeDuration() {
        ActiveSession s = new ActiveSession();
        s.setDurationSeconds(-1);
        assertEquals("Active", s.getDurationFormatted());
    }

    @Test
    @DisplayName("getDurationFormatted returns 00:00:00 for zero seconds")
    void getDurationFormatted_zero() {
        ActiveSession s = new ActiveSession();
        s.setDurationSeconds(0);
        assertEquals("00:00:00", s.getDurationFormatted());
    }

    @Test
    @DisplayName("getDurationFormatted handles large values")
    void getDurationFormatted_largeValue() {
        ActiveSession s = new ActiveSession();
        s.setDurationSeconds(86400); // 24 hours
        assertEquals("24:00:00", s.getDurationFormatted());
    }

    @Test
    @DisplayName("Getters and setters work correctly")
    void gettersSetters() {
        ActiveSession s = new ActiveSession();
        s.setHistoryId(42);
        s.setUsername("alice");
        s.setConnectionName("Web Server");
        s.setRemoteHost("192.168.1.1");

        assertEquals(42,           s.getHistoryId());
        assertEquals("alice",      s.getUsername());
        assertEquals("Web Server", s.getConnectionName());
        assertEquals("192.168.1.1",s.getRemoteHost());
    }
}
