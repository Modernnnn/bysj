package com.example.monitoringsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DataController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/collect_data")
    public String collectData(@RequestBody Map<String, Object> data) {
        int pcId = (int) data.get("pc_id");
        double cpuUsage = (double) data.get("cpu_usage");
        double memoryUsage = (double) data.get("memory_usage");

        // 存储到MySQL
        jdbcTemplate.update("INSERT INTO data (pc_id, cpu_usage, memory_usage) VALUES (?, ?, ?)", pcId, cpuUsage, memoryUsage);

        // 缓存到Redis
        redisTemplate.opsForValue().set("latest_metrics", String.format("CPU: %.2f%%, Memory: %.2f%%", cpuUsage, memoryUsage));

        return "Data received";
    }
}
