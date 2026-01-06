package com.vault.ai.common.controller;

import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final Driver neo4jDriver;

    @GetMapping
    public Map<String, String> checkHealth() {
        String postgresStatus = "UP";
        try { jdbcTemplate.execute("SELECT 1"); } catch (Exception e) { postgresStatus = "DOWN: " + e.getMessage(); }

        String neo4jStatus = "UP";
        try (Session session = neo4jDriver.session()) {
            session.run("RETURN 1");
        } catch (Exception e) { neo4jStatus = "DOWN: " + e.getMessage(); }

        return Map.of(
                "postgres", postgresStatus,
                "neo4j", neo4jStatus,
                "status", (postgresStatus.equals("UP") && neo4jStatus.equals("UP")) ? "HEALTHY" : "UNHEALTHY"
        );
    }
}