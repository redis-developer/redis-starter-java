package io.redis.todoapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.redis.todoapp.db.RedisClient;
import redis.clients.jedis.UnifiedJedis;

@Configuration
public class JedisConfig {
    @Bean
    public UnifiedJedis getInstance() {
        App.loadDotEnv();
        var redis = new RedisClient();

        return redis.getClient();
    }
}