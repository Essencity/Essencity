package com.xiaohongshu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    private static final Instant START_TIME = Instant.now();

    private final DataSource dataSource;
    private final String appVersion;
    private final String appName;

    public HealthController(DataSource dataSource,
                            @Value("${info.app.version:unknown}") String appVersion,
                            @Value("${info.app.name:unknown}") String appName) {
        this.dataSource = dataSource;
        this.appVersion = appVersion;
        this.appName = appName;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "healthy");
        result.put("timestamp", DateTimeFormatter.ISO_INSTANT
                .withZone(ZoneId.of("Asia/Shanghai"))
                .format(Instant.now()));
        result.put("app", appName);
        result.put("version", appVersion);

        // uptime
        Duration uptime = Duration.between(START_TIME, Instant.now());
        result.put("uptime", formatDuration(uptime));

        // database
        result.put("database", checkDatabase());

        // memory
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long used = memoryBean.getHeapMemoryUsage().getUsed();
        long max = memoryBean.getHeapMemoryUsage().getMax();
        Map<String, String> memory = new LinkedHashMap<>();
        memory.put("used", formatBytes(used));
        memory.put("max", formatBytes(max));
        memory.put("free", formatBytes(max - used));
        result.put("memory", memory);

        log.debug("Health check: status=healthy");
        return ResponseEntity.ok(result);
    }

    private String checkDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(3) ? "connected" : "disconnected";
        } catch (Exception e) {
            log.warn("Health check - database connection failed: {}", e.getMessage());
            return "disconnected";
        }
    }

    private static String formatDuration(Duration d) {
        long days = d.toDays();
        long hours = d.toHours() % 24;
        long minutes = d.toMinutes() % 60;
        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours, minutes);
        }
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        }
        return String.format("%dm", minutes);
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + "KB";
        return (bytes / (1024 * 1024)) + "MB";
    }
}
