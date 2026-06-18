package com.guacamole.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConnectionStat Model Tests")
class ConnectionStatTest {

    @Test
    @DisplayName("getTotalDurationFormatted formats correctly")
    void getTotalDurationFormatted() {
        ConnectionStat s = new ConnectionStat();
        s.setTotalDurationSeconds(7325); // 2h 2m 5s
        assertEquals("02:02:05", s.getTotalDurationFormatted());
    }

    @Test
    @DisplayName("getAvgDurationFormatted formats correctly")
    void getAvgDurationFormatted() {
        ConnectionStat s = new ConnectionStat();
        s.setAvgDurationSeconds(3600); // 1h
        assertEquals("01:00:00", s.getAvgDurationFormatted());
    }

    @Test
    @DisplayName("Zero duration formats as 00:00:00")
    void zeroDuration() {
        ConnectionStat s = new ConnectionStat();
        s.setTotalDurationSeconds(0);
        assertEquals("00:00:00", s.getTotalDurationFormatted());
    }

    @Test
    @DisplayName("All getters and setters work")
    void gettersSetters() {
        ConnectionStat s = new ConnectionStat();
        s.setConnectionName("DB Server");
        s.setTotalSessions(10);
        s.setMaxConcurrent(5);

        assertEquals("DB Server", s.getConnectionName());
        assertEquals(10, s.getTotalSessions());
        assertEquals(5,  s.getMaxConcurrent());
    }
}
