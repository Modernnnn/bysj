package com.example.monitoringsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ReaderApplication implements CommandLineRunner {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(ReaderApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Latest Metrics: " + getLatestMetrics());
        System.out.println("Historical Metrics: " + getHistoricalMetrics(1,10));
    }

    public String getLatestMetrics() {
        return redisTemplate.opsForValue().get("latest_metrics");
    }

    public List<Map<String, Object>> getHistoricalMetrics(int limit) {
        return jdbcTemplate.queryForList("SELECT pc_id, cpu_usage, memory_usage, timestamp FROM data ORDER BY timestamp DESC LIMIT ?", limit);
    }
    public List<Map<String, Object>> getHistoricalMetrics(int pc, int limit) {
        return jdbcTemplate.queryForList("SELECT pc_id, cpu_usage, memory_usage, timestamp FROM data WHERE pc_id=? ORDER BY timestamp DESC LIMIT ?", pc, limit);
    }
}
